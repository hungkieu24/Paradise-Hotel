# Staff Business Functions (from Java code) -> Clean Architecture CQRS Blueprint (ASP.NET Web API + MySQL)

Tài liệu này chuyển hoá các nghiệp vụ Staff trong repo `hungkieu24/Paradise-Hotel` (Java Servlet/JSP) sang mô hình Clean Architecture + CQRS để implement bằng ASP.NET Web API + MySQL.

---

## 1) Scope & Principles

### 1.1 Nguồn nghiệp vụ bám theo code
Các module Staff trong Java:
- Bookings list + actions: `StaffBookingsListServlet`, `StaffBookingActionServlet`, `staff-bookings-list.jsp`
- Checkout preview: `CheckoutServlet`, `staff-checkout.jsp`
- Room assignment: `RoomAssignmentServlet`, `RoomAssignmentViewServlet`, `staff-assign-rooms.jsp`, `staff-room-assignments-view.jsp`
- Rooms list + bulk update: `StaffRoomServlet`, `staff-rooms.jsp`
- Walk-in booking: `SearchGuestServlet`, `CreateWalkInBookingServlet`
- View user info by booking: `ViewUserInfoServlet`
- Availability AJAX: `AjaxBookedQuantityServlet`

### 1.2 Các rule xuyên suốt (must-have)
1) **Staff chỉ thao tác dữ liệu thuộc Branch của mình** (BranchId từ identity/claim).
2) Booking là state machine:
   - Check-in: chỉ `Paid`/`Pending` và **không trước giờ check-in**.
   - Confirm checkout: chỉ `CheckedIn` -> `Completed`; rooms -> `Available`.
   - Complete assignment: `Paid` -> `CheckedIn`, else -> `Assigned`.
3) RoomStatus hợp lệ: `Available | Booked | Occupied | Maintenance`.

---

## 2) Clean Architecture Structure

### 2.1 Projects
- `ParadiseHotel.Domain`
  - Entities, ValueObjects, Enums, Domain exceptions
- `ParadiseHotel.Application`
  - CQRS (MediatR): Commands/Queries + Handlers
  - DTOs, Interfaces Repositories, `IUnitOfWork`, `IClock`, `ICurrentUser`
- `ParadiseHotel.Infrastructure`
  - EF Core MySQL (`Pomelo.EntityFrameworkCore.MySql`)
  - Repositories + UnitOfWork + migrations
- `ParadiseHotel.Api`
  - Controllers/Endpoints, AuthN/AuthZ, Filters

### 2.2 Identity
Nên dùng JWT claims:
- `sub`: UserId
- `role`: Staff/Manager/Admin
- `branch_id`: int

Application service:
```csharp
public interface ICurrentUser {
  string UserId { get; }
  UserRole Role { get; }
  int? BranchId { get; }
}
```

---

## 3) Domain Model tối thiểu

### 3.1 Enums
```csharp
public enum BookingStatus { Pending, Paid, Assigned, CheckedIn, Completed, Cancelled }
public enum RoomStatus { Available, Booked, Occupied, Maintenance }
public enum PaidStatus { Unpaid, Paid }        // Booking services
public enum UserRole { Staff, Manager, Admin, Customer }
```

### 3.2 Entities (gợi ý fields)
- `UserAccount`: Id, Role, BranchId, Username, ...
- `Booking`: Id, BranchId, UserId, Status, CheckInAt, CheckOutAt, TotalPrice?, PaymentStatus?
- `Room`: Id, BranchId, RoomTypeId, RoomNumber, Status
- `BookingRoom`: BookingId, RoomId (association)
- `BookingService`: BookingId, ServiceId, Quantity, PaidStatus
- `Service`: Id, BranchId, Name, IsActive, Price

---

## 4) CQRS Use Cases / Functions (from code)

> Quy ước: mọi Handler phải validate `currentUser.BranchId` và enforce booking/room/service thuộc branch.

### 4.1 Bookings list (StaffBookingsListServlet)
**Query**
```csharp
public sealed record GetBranchBookingsQuery(
  int BranchId,
  string? Keyword,
  BookingStatus? Status,
  DateOnly? FromDate,
  DateOnly? ToDate,
  int Page = 1,
  int PageSize = 10
) : IRequest<PagedResult<BookingListItemDto>>;
```

**Rules**
- PageSize chỉ {5,10,15}. Nếu khác -> default 10.
- Query theo branchId + filters + paging.

**Output DTO**
```csharp
public sealed record BookingListItemDto(
  int Id,
  string CustomerName,
  BookingStatus Status,
  DateTime CheckInAt,
  DateTime CheckOutAt,
  decimal? TotalPrice
);
```

---

### 4.2 Check-in booking (StaffBookingActionServlet action=checkin)
**Command**
```csharp
public sealed record CheckInBookingCommand(int BookingId) : IRequest<Result>;
```

**Rules (từ code)**
1) Booking phải thuộc branch staff.
2) Status hợp lệ: `Paid` hoặc `Pending`.
3) Không check-in trước giờ: `now >= booking.CheckInAt`.
4) Update booking status -> `CheckedIn`.
5) Nếu booking đã gán phòng -> update các phòng đó -> `Occupied`.
6) Transaction commit.

---

### 4.3 Checkout preview (CheckoutServlet doGet)
**Query**
```csharp
public sealed record GetCheckoutPreviewQuery(int BookingId)
  : IRequest<CheckoutPreviewDto>;
```

**Rules**
- Booking thuộc branch staff.
- Booking status phải `CheckedIn`.

**CheckoutPreviewDto (gợi ý)**
- Booking summary, customer info (+rank), assigned rooms, booking services, totals.

---

### 4.4 Confirm checkout (StaffBookingActionServlet action=confirmcheckout)
**Command**
```csharp
public sealed record ConfirmCheckoutCommand(int BookingId) : IRequest<Result>;
```

**Rules**
- Booking thuộc branch staff.
- Booking status phải `CheckedIn`.
- Update booking status -> `Completed`.
- Release rooms -> `Available`.
- Transaction commit.

---

## 5) Room Assignment

### 5.1 Room assignment screen (RoomAssignmentServlet doGet + staff-assign-rooms.jsp)
**Query**
```csharp
public sealed record GetRoomAssignmentScreenQuery(int BookingId)
  : IRequest<RoomAssignmentScreenDto>;
```

**RoomAssignmentScreenDto**
- booking info
- requiredQuantities per roomType
- assigned counts per roomType
- available rooms grouped by roomType
- available services list (active services by branch)

---

### 5.2 Assign room to booking (RoomAssignmentServlet assignRoom)
**Command**
```csharp
public sealed record AssignRoomToBookingCommand(int BookingId, int RoomId)
  : IRequest<Result>;
```

**Rules**
- Booking & Room thuộc branch staff.
- Room có thể assign (thường status Available; không maintenance; không conflict).
- Upsert association booking_rooms.
- Update room status -> `Occupied`.
- Commit.

---

### 5.3 Remove room assignment (RoomAssignmentServlet removeRoomAssignment)
**Command**
```csharp
public sealed record RemoveRoomAssignmentCommand(int BookingId, int RoomId)
  : IRequest<Result>;
```

**Rules**
- Booking & Room thuộc branch staff.
- Remove association.
- (Recommended) nếu room không còn gán booking nào khác -> set status `Available`.
- Commit.

---

### 5.4 Complete assignment (RoomAssignmentServlet completeAssignment)
**Command**
```csharp
public sealed record CompleteRoomAssignmentCommand(int BookingId)
  : IRequest<Result>;
```

**Rule (từ code)**
- Nếu booking.Status == Paid -> set `CheckedIn`
- else -> set `Assigned`

---

### 5.5 Add/Update service to booking (RoomAssignmentServlet addServiceToBooking)
**Command**
```csharp
public sealed record AddOrUpdateBookingServiceCommand(
  int BookingId,
  int ServiceId,
  int Quantity,
  PaidStatus PaidStatus = PaidStatus.Unpaid
) : IRequest<Result>;
```

**Rules**
- Quantity > 0
- Service thuộc branch staff và active
- Upsert booking_service row

---

### 5.6 Remove service from booking
**Command**
```csharp
public sealed record RemoveBookingServiceCommand(int BookingId, int ServiceId)
  : IRequest<Result>;
```

---

## 6) Room Assignment List View (RoomAssignmentViewServlet)

**Query**
```csharp
public sealed record GetRoomAssignmentsQuery(
  int BranchId,
  string? Status,
  DateOnly? Date,
  string? Search,
  int Page = 1,
  int PageSize = 20
) : IRequest<PagedResult<RoomAssignmentListItemDto>>;
```

**Stats Query**
```csharp
public sealed record GetRoomAssignmentStatsQuery(int BranchId)
  : IRequest<RoomAssignmentStatsDto>;
```

**Rules**
- Staff/Manager only.
- Branch-scoped.

---

## 7) Rooms list + bulk status update (StaffRoomServlet)

### 7.1 Rooms list
**Query**
```csharp
public sealed record GetRoomsQuery(
  int BranchId,
  string? Keyword,
  RoomStatus? Status,
  int Page = 1,
  int PageSize = 8
) : IRequest<PagedResult<RoomListItemDto>>;
```

### 7.2 Bulk update status (StaffRoomServlet doPost)
**Command**
```csharp
public sealed record BulkUpdateRoomStatusCommand(
  IReadOnlyList<RoomStatusUpdateItem> Updates
) : IRequest<BulkResult>;

public sealed record RoomStatusUpdateItem(int RoomId, RoomStatus NewStatus);
```

**Rules**
- Mỗi room phải thuộc branch staff.
- NewStatus chỉ thuộc enum.
- Nên trả success/fail list.

---

## 8) Walk-in Booking (SearchGuestServlet + CreateWalkInBookingServlet)

### 8.1 Prepare walk-in booking data
**Query**
```csharp
public sealed record PrepareWalkInBookingQuery(DateOnly CheckInDate, DateOnly CheckOutDate)
  : IRequest<WalkInPrepareDto>;
```

**Rules**
- CheckInDate < CheckOutDate.
- Branch-scoped:
  - room types by branch
  - available rooms by branch & date range
  - active services by branch

### 8.2 Create walk-in booking (simple)
**Command**
```csharp
public sealed record CreateWalkInBookingCommand(
  string GuestId,
  int RoomId,
  DateTime CheckInAt,
  DateTime CheckOutAt
) : IRequest<Result<int>>; // returns bookingId
```

**Rules**
- Required fields.
- CheckInAt < CheckOutAt.
- Room belongs to branch.
- Create booking + update room status -> `Booked`.
- Commit.

---

## 9) View customer info by booking (ViewUserInfoServlet)
**Query**
```csharp
public sealed record GetCustomerInfoByBookingQuery(int BookingId)
  : IRequest<CustomerBookingInfoDto>;
```

**Rules**
- Booking belongs to branch staff.
- Return customer + rank + booking detail.

---

## 10) Availability (AjaxBookedQuantityServlet)
**Query**
```csharp
public sealed record GetBookedQuantityQuery(
  int RoomTypeId,
  DateOnly CheckInDate,
  DateOnly CheckOutDate
) : IRequest<int>;
```

**Rules**
- CheckInDate < CheckOutDate
- BranchId from current user
- Return booked quantity (int)

---

## 11) API Endpoint Mapping (ASP.NET Web API)

### Bookings
- `GET    /api/staff/bookings?keyword=&status=&fromDate=&toDate=&page=&pageSize=`
- `POST   /api/staff/bookings/{bookingId}/check-in`
- `GET    /api/staff/bookings/{bookingId}/checkout/preview`
- `POST   /api/staff/bookings/{bookingId}/checkout/confirm`
- `GET    /api/staff/bookings/{bookingId}/customer`

### Room assignment
- `GET    /api/staff/bookings/{bookingId}/room-assignment/screen`
- `POST   /api/staff/bookings/{bookingId}/rooms/{roomId}`        (assign)
- `DELETE /api/staff/bookings/{bookingId}/rooms/{roomId}`        (remove)
- `POST   /api/staff/bookings/{bookingId}/room-assignment/complete`
- `PUT    /api/staff/bookings/{bookingId}/services/{serviceId}`  (upsert)
- `DELETE /api/staff/bookings/{bookingId}/services/{serviceId}`

### Rooms
- `GET    /api/staff/rooms?keyword=&status=&page=`
- `PUT    /api/staff/rooms/status:bulk`

### Walk-in
- `GET    /api/staff/walkin/prepare?checkInDate=&checkOutDate=`
- `POST   /api/staff/walkin`

### Availability
- `GET    /api/staff/availability/booked-quantity?roomTypeId=&checkInDate=&checkOutDate=`

---

## 12) Infrastructure Notes (EF Core MySQL)
- Provider: `Pomelo.EntityFrameworkCore.MySql`
- Các nghiệp vụ update nhiều bảng (booking + rooms) phải chạy trong 1 transaction:
  - `IUnitOfWork.BeginTransactionAsync()`
  - commit/rollback

Repository interfaces (minimum):
```csharp
public interface IBookingRepository {
  Task<Booking?> GetByIdAndBranchAsync(int bookingId, int branchId, CancellationToken ct);
  Task UpdateAsync(Booking booking, CancellationToken ct);
  // Search/paging methods...
}

public interface IRoomRepository {
  Task<Room?> GetByIdAndBranchAsync(int roomId, int branchId, CancellationToken ct);
  Task<List<Room>> GetAssignedRoomsByBookingAsync(int bookingId, CancellationToken ct);
  Task UpdateAsync(Room room, CancellationToken ct);
  Task UpdateRangeAsync(IEnumerable<Room> rooms, CancellationToken ct);
}
```

---

## 13) Recommended Implementation Order
1) Auth + `ICurrentUser` (role + branchId)
2) `GetBranchBookingsQuery`
3) `CheckInBookingCommand`
4) `ConfirmCheckoutCommand`
5) Rooms list + bulk update
6) Room assignment screen + assign/remove + complete
7) Walk-in prepare + create
8) Checkout preview (calculation totals)
9) Booking services (upsert/remove)
10) Availability query
