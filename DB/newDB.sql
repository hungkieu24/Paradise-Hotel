USE master
GO

-- Drop database if it exists and create new
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'HotelBookingSystemDB')
BEGIN
    ALTER DATABASE HotelBookingSystemDB SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE HotelBookingSystemDB;
END
GO
CREATE DATABASE HotelBookingSystemDB
GO

USE HotelBookingSystemDB
GO

-- 1. USER & ROLE
CREATE TABLE UserAccount (
    id VARCHAR(10) PRIMARY KEY,
    username NVARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255),
    fullname NVARCHAR(255),
    email VARCHAR(100) UNIQUE NOT NULL,
    login_type VARCHAR(20) CHECK (login_type IN ('Local', 'Google')) NOT NULL DEFAULT 'Local',
    avatar_url VARCHAR(255),
    role VARCHAR(20) CHECK (role IN ('Customer', 'Staff', 'Manager', 'Admin', 'HotelOwner')) NOT NULL,
    status VARCHAR(10) CHECK (status IN ('Active', 'Inactive', 'Banned')) DEFAULT 'Active',
    created_at DATETIME DEFAULT GETDATE(),
    last_login_at DATETIME NULL,
    phonenumber VARCHAR(20),
    branch_id INT NULL,
    is_deleted BIT DEFAULT 0
);

-- 2. HOTEL BRANCH
CREATE TABLE HotelBranch (
    id INT PRIMARY KEY IDENTITY(1, 1),
    name NVARCHAR(255) NOT NULL,
    address NVARCHAR(MAX) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    image_url VARCHAR(255),
    owner_id VARCHAR(10) NOT NULL,
    manager_id VARCHAR(10),
    created_at DATETIME DEFAULT GETDATE(),
    is_deleted BIT DEFAULT 0
);

-- 3. ROOM TYPE, ROOM, AMENITY
CREATE TABLE RoomType (
    id INT PRIMARY KEY IDENTITY(1, 1),
    name VARCHAR(100),
    description NVARCHAR(MAX),
    base_price DECIMAL(18,2) NOT NULL,
    capacity_adult INT NOT NULL,
    capacity_child INT NOT NULL,
    branch_id INT NOT NULL,
    image_url VARCHAR(255),
    is_deleted BIT DEFAULT 0
);

CREATE TABLE Room (
    id INT PRIMARY KEY IDENTITY(1, 1),
    room_number VARCHAR(20) NOT NULL,
    branch_id INT NOT NULL,
    room_type_id INT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('Available', 'Booked', 'Occupied', 'Maintenance')) DEFAULT 'Available',
    image_url VARCHAR(255),
    is_deleted BIT DEFAULT 0,
    CONSTRAINT UQ_Room_Branch_RoomNumber UNIQUE (branch_id, room_number)
);

CREATE TABLE Amenity (
    id INT PRIMARY KEY IDENTITY(1,1),
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(255),
    is_deleted BIT DEFAULT 0
);

CREATE TABLE RoomAmenity (
    room_id INT,
    amenity_id INT,
    PRIMARY KEY (room_id, amenity_id)
);

-- 4. SERVICE
CREATE TABLE Service (
    id INT PRIMARY KEY IDENTITY(1, 1),
    name VARCHAR(100),
    description NVARCHAR(MAX),
    price DECIMAL(18,2),
    branch_id INT NOT NULL,
    status VARCHAR(10) CHECK (status IN ('Active', 'Inactive')) DEFAULT 'Active',
    image_url VARCHAR(255),
    is_deleted BIT DEFAULT 0
);

-- 5. BOOKING, BOOKING DETAIL
CREATE TABLE Booking (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10) NOT NULL,
    created_by VARCHAR(10),
    booking_time DATETIME DEFAULT GETDATE(),
    check_in DATETIME NOT NULL,
    check_out DATETIME NOT NULL,
    status VARCHAR(20) CHECK (status IN 
        ('Pending', 'Paid', 'CheckedIn', 'CheckedOut', 
         'Completed', 'Cancelled', 'NoShow')
    ) DEFAULT 'Pending',
    total_price DECIMAL(18,2) NOT NULL,
    refund_amount DECIMAL(18,2),
    payment_status VARCHAR(10) CHECK (payment_status IN ('Unpaid', 'Paid')) DEFAULT 'Unpaid',
    cancel_reason NVARCHAR(MAX),
    cancel_time DATETIME,
    promotion_id INT,
    branch_id INT NOT NULL,
    note NVARCHAR(MAX),
    is_deleted BIT DEFAULT 0
);

CREATE TABLE BookingVoucher (
    booking_id INT,
    voucher_id INT,
    PRIMARY KEY (booking_id, voucher_id),
    used_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE BookingRoomType (
    booking_id INT NOT NULL,
    room_type_id INT NOT NULL,
    quantity INT NOT NULL,
    price_per_room DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (booking_id, room_type_id)
);

CREATE TABLE RoomAssignment (
    booking_id INT NOT NULL,
    room_id INT NOT NULL,
    assigned_at DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (booking_id, room_id)
);

CREATE TABLE BookingService (
    booking_id INT,
    service_id INT,
    quantity INT,
    paid_status VARCHAR(10) CHECK (paid_status IN ('Unpaid', 'Paid')) DEFAULT 'Unpaid',
    PRIMARY KEY (booking_id, service_id)
);

-- 6. VOUCHER, PROMOTION
CREATE TABLE Voucher (
    id INT PRIMARY KEY IDENTITY(1, 1),
    code VARCHAR(50) UNIQUE NOT NULL,
    description NVARCHAR(MAX),
    discount_percent INT,
    discount_amount DECIMAL(18,2),
    min_price DECIMAL(18,2),
    total_quantity INT,
    used_quantity INT DEFAULT 0,
    branch_id INT NOT NULL,
    valid_from DATETIME,
    valid_to DATETIME,
    status VARCHAR(10) CHECK (status IN ('Active', 'Inactive', 'Expired')) DEFAULT 'Active',
    is_deleted BIT DEFAULT 0
);

CREATE TABLE SeasonalPromotion (
    id INT PRIMARY KEY IDENTITY(1, 1),
    name NVARCHAR(100),
    description NVARCHAR(MAX),
    discount_percent DECIMAL(5,2),
    discount_amount DECIMAL(18,2),
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    branch_id INT NOT NULL,
    room_type_id INT NOT NULL,
    status VARCHAR(10) CHECK (status IN ('Active', 'Inactive', 'Expired')) DEFAULT 'Active',
    is_deleted BIT DEFAULT 0
);

-- 7. FEEDBACK
CREATE TABLE Feedback (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10) NOT NULL,
    booking_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comment NVARCHAR(MAX),
    image_url NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    status VARCHAR(10) CHECK (status IN ('Visible', 'Hidden', 'Blocked')) DEFAULT 'Visible',
    admin_action VARCHAR(10) CHECK (admin_action IN ('None', 'Warned', 'Banned')) DEFAULT 'None',
    is_deleted BIT DEFAULT 0
);

-- 8. PAYMENT, INVOICE, EXPENSE
CREATE TABLE VNPayPayment (
    id INT PRIMARY KEY IDENTITY(1, 1),
    booking_id INT NOT NULL,
    amount DECIMAL(18,2),
    status VARCHAR(10) CHECK (status IN ('Pending', 'Completed', 'Failed', 'Refunded')) DEFAULT 'Pending',
    paid_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE VNPayTransaction (
    id INT PRIMARY KEY IDENTITY(1, 1),
    payment_id INT NOT NULL,
    vnp_TxnRef VARCHAR(100),
    vnp_TransactionNo VARCHAR(100),
    vnp_ResponseCode VARCHAR(10),
    vnp_Amount DECIMAL(18,2),
    vnp_BankCode VARCHAR(50),
    vnp_CardType VARCHAR(50),
    vnp_SecureHash VARCHAR(255),
    is_refunded BIT,
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE Invoice (
    id INT PRIMARY KEY IDENTITY(1, 1),
    booking_id INT NOT NULL,
    total_amount DECIMAL(18,2),
    issued_at DATETIME DEFAULT GETDATE(),
    pdf_url VARCHAR(255),
    image_url VARCHAR(255)
);

CREATE TABLE Expense (
    id INT PRIMARY KEY IDENTITY(1,1),
    branch_id INT NOT NULL,
    expense_type VARCHAR(50),
    amount DECIMAL(18,2) NOT NULL,
    description NVARCHAR(MAX),
    expense_date DATETIME DEFAULT GETDATE(),
    created_by VARCHAR(10)
);

-- 9. LOYALTY POINT & REDEEM
CREATE TABLE LoyaltyPoint (
    user_id VARCHAR(10) PRIMARY KEY,
    points INT,
    level VARCHAR(10) CHECK (level IN ('Member', 'Silver', 'Gold', 'VIP')) DEFAULT 'Member',
    last_updated DATETIME DEFAULT GETDATE(),
    expired_at DATETIME
);

CREATE TABLE PointTransaction (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10),
    change_type VARCHAR(20) CHECK (change_type IN ('Earn', 'Redeem', 'Adjustment')),
    points_changed INT,
    reason NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE PointRedeemVoucher (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10),
    voucher_id INT,
    points_used INT,
    redeemed_at DATETIME DEFAULT GETDATE(),
    expired_at DATETIME
);

-- 10. AI HISTORY, MEMBER TIER HISTORY, BACKUP HISTORY
CREATE TABLE ChatAIHistory (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10),
    message NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    violation VARCHAR(10)
);

CREATE TABLE MemberTierHistory (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10),
    old_level VARCHAR(10) CHECK (old_level IN ('Member', 'Silver', 'Gold', 'VIP')),
    new_level VARCHAR(10) CHECK (new_level IN ('Member', 'Silver', 'Gold', 'VIP')),
    changed_at DATETIME DEFAULT GETDATE(),
    reason NVARCHAR(MAX)
);

CREATE TABLE BackupHistory (
    id INT PRIMARY KEY IDENTITY(1,1),
    backup_time DATETIME DEFAULT GETDATE(),
    backup_type VARCHAR(20) CHECK (backup_type IN ('FULL', 'PARTIAL')),
    backup_path NVARCHAR(500) NOT NULL,
    file_size_mb FLOAT,
    is_deleted BIT DEFAULT 0
);

-- 11. PERMISSION, NOTIFICATION, CART
CREATE TABLE Permission (
    id INT PRIMARY KEY IDENTITY(1, 1),
    role VARCHAR(20) CHECK (role IN ('Customer', 'Staff', 'Manager', 'Admin', 'HotelOwner')) NOT NULL,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(10) CHECK (action IN ('Create', 'Read', 'Update', 'Delete')) NOT NULL,
    allowed BIT
);

CREATE TABLE Notification (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10),
    title VARCHAR(255),
    message NVARCHAR(MAX),
    type VARCHAR(20) CHECK (type IN (
        'System', 'Promotion', 'Booking', 'Payment',
        'Feedback', 'Chat', 'LoyaltyPoint', 'TierUpgrade'
    )) DEFAULT 'System',
    status VARCHAR(10) CHECK (status IN ('Unread', 'Read')) DEFAULT 'Unread',
    created_at DATETIME DEFAULT GETDATE(),
    read_at DATETIME,
    related_booking_id INT,
    related_point_transaction_id INT,
    related_member_tier_history_id INT
);

CREATE TABLE CartRoomType (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10) NOT NULL,
    room_type_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT (1),
    added_at DATETIME DEFAULT (GETDATE()),
    CONSTRAINT UQ_CartRoomType_User_RoomType UNIQUE(user_id, room_type_id)
);

-- 12. BENEFIT RANK
CREATE TABLE BenefitRank (
    id INT PRIMARY KEY IDENTITY(1,1),
    level VARCHAR(10) CHECK (level IN ('Member', 'Silver', 'Gold', 'VIP')) UNIQUE NOT NULL,
    point_rate DECIMAL(5,2) NOT NULL,
    discount_percent DECIMAL(5,2),
    benefit NVARCHAR(MAX),
    is_deleted BIT DEFAULT 0
);

CREATE TABLE VoucherRedemptionRule (
    id INT PRIMARY KEY IDENTITY(1,1),
    voucher_id INT NOT NULL,
    required_points INT NOT NULL,
    required_tier VARCHAR(10) CHECK (required_tier IN ('Member', 'Silver', 'Gold', 'VIP')) NULL,
    is_active BIT DEFAULT 1,
    FOREIGN KEY (voucher_id) REFERENCES Voucher(id)
);

-- 13. FOREIGN KEYS
ALTER TABLE UserAccount ADD CONSTRAINT FK_UserAccount_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE HotelBranch ADD CONSTRAINT FK_HotelBranch_Owner FOREIGN KEY (owner_id) REFERENCES UserAccount (id);
ALTER TABLE HotelBranch ADD CONSTRAINT FK_HotelBranch_Manager FOREIGN KEY (manager_id) REFERENCES UserAccount (id);
ALTER TABLE RoomType ADD CONSTRAINT FK_RoomType_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE Room ADD CONSTRAINT FK_Room_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE Room ADD CONSTRAINT FK_Room_RoomType FOREIGN KEY (room_type_id) REFERENCES RoomType (id);
ALTER TABLE RoomAmenity ADD CONSTRAINT FK_RoomAmenity_Room FOREIGN KEY (room_id) REFERENCES Room (id);
ALTER TABLE RoomAmenity ADD CONSTRAINT FK_RoomAmenity_Amenity FOREIGN KEY (amenity_id) REFERENCES Amenity (id);
ALTER TABLE Booking ADD CONSTRAINT FK_Booking_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE Booking ADD CONSTRAINT FK_Booking_CreatedBy FOREIGN KEY (created_by) REFERENCES UserAccount (id);
ALTER TABLE Booking ADD CONSTRAINT FK_Booking_Promotion FOREIGN KEY (promotion_id) REFERENCES SeasonalPromotion (id);
ALTER TABLE Booking ADD CONSTRAINT FK_Booking_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE BookingVoucher ADD CONSTRAINT FK_BookingVoucher_Booking FOREIGN KEY (booking_id) REFERENCES Booking (id);
ALTER TABLE BookingVoucher ADD CONSTRAINT FK_BookingVoucher_Voucher FOREIGN KEY (voucher_id) REFERENCES Voucher (id);
ALTER TABLE BookingRoomType ADD CONSTRAINT FK_BookingRoomType_Booking FOREIGN KEY (booking_id) REFERENCES Booking (id);
ALTER TABLE BookingRoomType ADD CONSTRAINT FK_BookingRoomType_RoomType FOREIGN KEY (room_type_id) REFERENCES RoomType (id);
ALTER TABLE RoomAssignment ADD CONSTRAINT FK_RoomAssignment_Booking FOREIGN KEY (booking_id) REFERENCES Booking (id);
ALTER TABLE RoomAssignment ADD CONSTRAINT FK_RoomAssignment_Room FOREIGN KEY (room_id) REFERENCES Room (id);
ALTER TABLE Service ADD CONSTRAINT FK_Service_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE BookingService ADD CONSTRAINT FK_BookingService_Booking FOREIGN KEY (booking_id) REFERENCES Booking (id);
ALTER TABLE BookingService ADD CONSTRAINT FK_BookingService_Service FOREIGN KEY (service_id) REFERENCES Service (id);
ALTER TABLE Feedback ADD CONSTRAINT FK_Feedback_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE Feedback ADD CONSTRAINT FK_Feedback_Booking FOREIGN KEY (booking_id) REFERENCES Booking (id);
ALTER TABLE VNPayPayment ADD CONSTRAINT FK_VNPayPayment_Booking FOREIGN KEY (booking_id) REFERENCES Booking (id);
ALTER TABLE VNPayTransaction ADD CONSTRAINT FK_VNPayTransaction_Payment FOREIGN KEY (payment_id) REFERENCES VNPayPayment (id);
ALTER TABLE Invoice ADD CONSTRAINT FK_Invoice_Booking FOREIGN KEY (booking_id) REFERENCES Booking (id);
ALTER TABLE Expense ADD CONSTRAINT FK_Expense_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE LoyaltyPoint ADD CONSTRAINT FK_LoyaltyPoint_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE PointTransaction ADD CONSTRAINT FK_PointTransaction_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE PointRedeemVoucher ADD CONSTRAINT FK_PointRedeemVoucher_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE PointRedeemVoucher ADD CONSTRAINT FK_PointRedeemVoucher_Voucher FOREIGN KEY (voucher_id) REFERENCES Voucher (id);
ALTER TABLE ChatAIHistory ADD CONSTRAINT FK_ChatAIHistory_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE MemberTierHistory ADD CONSTRAINT FK_MemberTierHistory_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE Notification ADD CONSTRAINT FK_Notification_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE Notification ADD CONSTRAINT FK_Notification_Booking FOREIGN KEY (related_booking_id) REFERENCES Booking (id);
ALTER TABLE Notification ADD CONSTRAINT FK_Notification_PointTransaction FOREIGN KEY (related_point_transaction_id) REFERENCES PointTransaction (id);
ALTER TABLE Notification ADD CONSTRAINT FK_Notification_MemberTierHistory FOREIGN KEY (related_member_tier_history_id) REFERENCES MemberTierHistory (id);
ALTER TABLE CartRoomType ADD CONSTRAINT FK_CartRoomType_User FOREIGN KEY (user_id) REFERENCES UserAccount (id);
ALTER TABLE CartRoomType ADD CONSTRAINT FK_CartRoomType_RoomType FOREIGN KEY (room_type_id) REFERENCES RoomType (id);
ALTER TABLE Voucher ADD CONSTRAINT FK_Voucher_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE SeasonalPromotion ADD CONSTRAINT FK_SeasonalPromotion_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE SeasonalPromotion ADD CONSTRAINT FK_SeasonalPromotion_RoomType FOREIGN KEY (room_type_id) REFERENCES RoomType (id);
GO

-- INSERT SAMPLE DATA
-- 1. UserAccount (with branch_id = NULL initially)
INSERT INTO UserAccount (
    id, username, password, fullname, email, login_type,
    avatar_url, role, status, created_at, phonenumber, branch_id, is_deleted, last_login_at
) VALUES
-- Active users with recent login times
('U001', 'john_doe', 'hashed_password1', N'John Doe', 'john.doe@email.com', 'Local', 
 'https://example.com/avatar1.jpg', 'Customer', 'Active', DATEADD(HOUR, -2, GETDATE()), '1234567890', NULL, 0, GETDATE()), -- Logged in 2 hours ago
('U002', 'jane_smith', 'hashed_password2', N'Jane Smith', 'jane.smith@email.com', 'Google', 
 'https://example.com/avatar2.jpg', 'Staff', 'Active', DATEADD(DAY, -1, GETDATE()), '0987654321', NULL, 0, GETDATE()), -- Logged in 1 day ago
('U003', 'mike_manager', 'hashed_password3', N'Mike Johnson', 'mike.johnson@email.com', 'Local', 
 'https://example.com/avatar3.jpg', 'Manager', 'Active', DATEADD(HOUR, -5, GETDATE()), '5551234567', NULL, 0, GETDATE()), -- Logged in 5 hours ago
('U004', 'anna_owner', 'hashed_password4', N'Anna Brown', 'anna.brown@email.com', 'Local', 
 'https://example.com/avatar4.jpg', 'HotelOwner', 'Active', DATEADD(DAY, -2, GETDATE()), '5559876543', NULL, 0, GETDATE()), -- Logged in 2 days ago
('U005', 'admin_user', 'hashed_password5', N'Admin User', 'admin@email.com', 'Local', 
 'https://example.com/avatar5.jpg', 'Admin', 'Active', DATEADD(HOUR, -1, GETDATE()), '5551112222', NULL, 0, GETDATE()), -- Logged in 1 hour ago
-- Inactive user, no recent login
('U006', 'truongminhquan', 'pass123', N'Trương Minh Quân', 'quan.truong@example.com', 
 'Local', NULL, 'Customer', 'Inactive', GETDATE(), '0901000001', NULL, 0, GETDATE()),
-- Banned user, no recent login
('U007', 'nguyenthuytrang', 'pass456', N'Nguyễn Thùy Trang', 'trang.nguyen@example.com', 
 'Local', NULL, 'Customer', 'Banned', GETDATE(), '0902000002', NULL, 0, GETDATE()),
-- Soft-deleted user, no recent login (status changed to 'Active' to satisfy CHECK constraint)
('U008', 'phamducthinh', 'pass789', N'Phạm Đức Thịnh', 'thinh.pham@example.com', 
 'Local', NULL, 'Customer', 'Active', GETDATE(), '0903000003', NULL, 1, GETDATE());
GO
-- 2. HotelBranch
INSERT INTO HotelBranch (name, address, phone, email, image_url, owner_id, manager_id, created_at, is_deleted) VALUES
(N'Sunshine Hotel Hanoi', N'123 Tran Phu, Hanoi', '0241234567', 'hanoi@sunshinehotel.com', 'https://example.com/hotel1.jpg', 'U004', 'U003', GETDATE(), 0),
(N'Sunshine Hotel Da Nang', N'456 Vo Nguyen Giap, Da Nang', '0236123456', 'danang@sunshinehotel.com', 'https://example.com/hotel2.jpg', 'U004', NULL, GETDATE(), 0),
(N'Sunshine Hotel HCMC', N'789 Le Loi, HCMC', '0281234567', 'hcmc@sunshinehotel.com', 'https://example.com/hotel3.jpg', 'U004', NULL, GETDATE(), 0),
(N'Sunshine Hotel Nha Trang', N'101 Tran Hung Dao, Nha Trang', '0258123456', 'nhatrang@sunshinehotel.com', 'https://example.com/hotel4.jpg', 'U004', NULL, GETDATE(), 0),
(N'Sunshine Hotel Phu Quoc', N'202 Duong Dong, Phu Quoc', '0297123456', 'phuquoc@sunshinehotel.com', 'https://example.com/hotel5.jpg', 'U004', NULL, GETDATE(), 0);

-- Update UserAccount to set branch_id
UPDATE UserAccount SET branch_id = 1 WHERE id IN ('U002', 'U004');
-- RoomType (5 rows)
INSERT INTO RoomType (name, description, base_price, capacity_adult, capacity_child, branch_id, image_url)
VALUES 
('Standard', 'Cozy room with basic amenities', 50.00, 2, 1, 1, 'standard.jpg'),
('Deluxe', 'Spacious room with sea view', 100.00, 3, 2, 1, 'deluxe.jpg'),
('Suite', 'Luxury suite with balcony', 200.00, 4, 2, 2, 'suite.jpg'),
('Family', 'Large room for families', 150.00, 4, 3, 3, 'family.jpg'),
('Single', 'Compact room for solo travelers', 40.00, 1, 0, 4, 'single.jpg');

-- Room (5 rows)
INSERT INTO Room (room_number, branch_id, room_type_id, status, image_url)
VALUES 
('101', 1, 1, 'Available', 'room101.jpg'),
('102', 1, 2, 'Booked', 'room102.jpg'),
('201', 2, 3, 'Occupied', 'room201.jpg'),
('301', 3, 4, 'Available', 'room301.jpg'),
('401', 4, 5, 'Maintenance', 'room401.jpg');

-- Amenity (5 rows)
INSERT INTO Amenity (name, description)
VALUES 
('WiFi', 'High-speed internet access'),
('Air Conditioning', 'Climate control unit'),
('Mini Bar', 'Refrigerated mini bar'),
('TV', 'Flat-screen television'),
('Safe', 'In-room safety deposit box');

-- RoomAmenity (5 rows)
INSERT INTO RoomAmenity (room_id, amenity_id)
VALUES 
(1, 1), (1, 2), (2, 3), (3, 4), (4, 5);

-- Service (5 rows)
INSERT INTO Service (name, description, price, branch_id, status, image_url)
VALUES 
('Breakfast', 'Buffet breakfast', 15.00, 1, 'Active', 'breakfast.jpg'),
('Spa', 'Relaxing spa treatment', 50.00, 1, 'Active', 'spa.jpg'),
('Laundry', 'Same-day laundry service', 10.00, 2, 'Active', 'laundry.jpg'),
('Airport Shuttle', 'Transportation to airport', 20.00, 3, 'Inactive', 'shuttle.jpg'),
('Room Service', '24/7 room service', 25.00, 4, 'Active', 'roomservice.jpg');

-- Booking (10 rows)
INSERT INTO Booking (user_id, created_by, check_in, check_out, status, total_price, payment_status, branch_id, note)
VALUES 
('U001', NULL, '2025-07-01 14:00:00', '2025-07-03 12:00:00', 'Pending', 100.00, 'Unpaid', 1, 'Early check-in requested'),
('U001', 'U002', '2025-07-05 14:00:00', '2025-07-07 12:00:00', 'Paid', 200.00, 'Paid', 1, NULL),
('U001', NULL, '2025-07-10 14:00:00', '2025-07-12 12:00:00', 'CheckedIn', 150.00, 'Paid', 2, 'Extra pillows'),
('U001', NULL, '2025-07-15 14:00:00', '2025-07-17 12:00:00', 'CheckedOut', 300.00, 'Paid', 2, NULL),
('U001', 'U002', '2025-07-20 14:00:00', '2025-07-22 12:00:00', 'Completed', 120.00, 'Paid', 3, 'Late checkout'),
('U001', NULL, '2025-07-25 14:00:00', '2025-07-27 12:00:00', 'Cancelled', 80.00, 'Unpaid', 3, 'Cancelled due to schedule change'),
('U001', NULL, '2025-08-01 14:00:00', '2025-08-03 12:00:00', 'NoShow', 90.00, 'Unpaid', 4, NULL),
('U001', 'U002', '2025-08-05 14:00:00', '2025-08-07 12:00:00', 'Pending', 110.00, 'Unpaid', 4, 'Payment pending'),
('U001', NULL, '2025-08-10 14:00:00', '2025-08-12 12:00:00', 'Paid', 130.00, 'Paid', 1, 'Special request for view'),
('U001', NULL, '2025-08-15 14:00:00', '2025-08-17 12:00:00', 'Paid', 250.00, 'Paid', 2, NULL);

-- Voucher (5 rows)
INSERT INTO Voucher (code, description, discount_percent, discount_amount, min_price, total_quantity, used_quantity, branch_id, valid_from, valid_to, status)
VALUES 
('DISC10', '10% off for first booking', 10, NULL, 50.00, 100, 10, 1, '2025-06-01', '2025-12-31', 'Active'),
('SAVE20', '20 USD off', NULL, 20.00, 100.00, 50, 5, 1, '2025-06-01', '2025-12-31', 'Active'),
('SUMMER25', 'Summer discount', 25, NULL, 150.00, 200, 20, 2, '2025-06-01', '2025-08-31', 'Active'),
('VIP50', 'VIP discount', NULL, 50.00, 200.00, 30, 2, 3, '2025-06-01', '2025-12-31', 'Active'),
('WELCOME15', 'Welcome offer', 15, NULL, 80.00, 150, 15, 4, '2025-06-01', '2025-12-31', 'Active'),
('VIPONLY70', '70% discount for VIP users only', 70, NULL, 300.00, 20, 0, 2, '2025-06-01', '2025-12-31', 'Active');
-- BookingVoucher (5 rows)
INSERT INTO BookingVoucher (booking_id, voucher_id, used_at)
VALUES 
(1, 1, '2025-07-01 10:00:00'),
(2, 2, '2025-07-05 10:00:00'),
(3, 3, '2025-07-10 10:00:00'),
(4, 4, '2025-07-15 10:00:00'),
(5, 5, '2025-07-20 10:00:00');

-- BookingRoomType (5 rows)
INSERT INTO BookingRoomType (booking_id, room_type_id, quantity, price_per_room)
VALUES 
(1, 1, 1, 50.00),
(2, 2, 1, 100.00),
(3, 3, 1, 150.00),
(4, 4, 2, 150.00),
(5, 5, 1, 40.00);

-- RoomAssignment (5 rows)
INSERT INTO RoomAssignment (booking_id, room_id)
VALUES 
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5);

-- BookingService (5 rows)
INSERT INTO BookingService (booking_id, service_id, quantity, paid_status)
VALUES 
(1, 1, 2, 'Paid'),
(2, 2, 1, 'Unpaid'),
(3, 3, 1, 'Paid'),
(4, 4, 2, 'Unpaid'),
(5, 5, 1, 'Paid');



-- SeasonalPromotion (5 rows)
INSERT INTO SeasonalPromotion (name, description, discount_percent, discount_amount, start_date, end_date, branch_id, room_type_id, status)
VALUES 
('Summer Sale', 'Summer discount on all rooms', 15.00, NULL, '2025-06-01', '2025-08-31', 1, 1, 'Active'),
('Winter Deal', 'Winter special offer', NULL, 30.00, '2025-12-01', '2026-02-28', 2, 2, 'Active'),
('Spring Promo', 'Spring getaway discount', 10.00, NULL, '2025-03-01', '2025-05-31', 3, 3, 'Active'),
('Fall Sale', 'Fall season discount', NULL, 25.00, '2025-09-01', '2025-11-30', 4, 4, 'Active'),
('Holiday Special', 'Holiday season offer', 20.00, NULL, '2025-12-15', '2026-01-05', 5, 5, 'Active');

-- Feedback (5 rows)
INSERT INTO Feedback (user_id, booking_id, rating, comment, image_url, status)
VALUES 
('U001', 1, 4, 'Great stay, friendly staff', 'feedback1.jpg', 'Visible'),
('U001', 2, 5, 'Amazing view and service', 'feedback2.jpg', 'Visible'),
('U001', 3, 3, 'Room was clean but small', 'feedback3.jpg', 'Visible'),
('U001', 4, 4, 'Good experience overall', 'feedback4.jpg', 'Hidden'),
('U001', 5, 5, 'Perfect family vacation', 'feedback5.jpg', 'Visible');

-- VNPayPayment (5 rows)
INSERT INTO VNPayPayment (booking_id, amount, status, paid_at)
VALUES 
(1, 100.00, 'Pending', '2025-07-01 10:00:00'),
(2, 200.00, 'Completed', '2025-07-05 10:00:00'),
(3, 150.00, 'Completed', '2025-07-10 10:00:00'),
(4, 300.00, 'Completed', '2025-07-15 10:00:00'),
(5, 120.00, 'Refunded', '2025-07-20 10:00:00');

-- VNPayTransaction (5 rows)
INSERT INTO VNPayTransaction (payment_id, vnp_TxnRef, vnp_TransactionNo, vnp_ResponseCode, vnp_Amount, vnp_BankCode, vnp_CardType, vnp_SecureHash, is_refunded)
VALUES 
(1, 'TXN001', '123456', '00', 100.00, 'NCB', 'VISA', 'hash1', 0),
(2, 'TXN002', '123457', '00', 200.00, 'VCB', 'MASTER', 'hash2', 0),
(3, 'TXN003', '123458', '00', 150.00, 'TPB', 'VISA', 'hash3', 0),
(4, 'TXN004', '123459', '00', 300.00, 'MBB', 'MASTER', 'hash4', 0),
(5, 'TXN005', '123460', '07', 120.00, 'ACB', 'VISA', 'hash5', 1);

-- Invoice (5 rows)
INSERT INTO Invoice (booking_id, total_amount, issued_at, pdf_url)
VALUES 
(1, 100.00, '2025-07-01 10:00:00', 'invoice1.pdf'),
(2, 200.00, '2025-07-05 10:00:00', 'invoice2.pdf'),
(3, 150.00, '2025-07-10 10:00:00', 'invoice3.pdf'),
(4, 300.00,'2025-07-15 10:00:00', 'invoice4.pdf'),
(5, 120.00, '2025-07-20 10:00:00', 'invoice5.pdf');

-- Expense (5 rows)
INSERT INTO Expense (branch_id, expense_type, amount, description, created_by)
VALUES 
(1, 'Utilities', 500.00, 'Electricity bill', 'U003'),
(2, 'Maintenance', 200.00, 'Room repairs', 'U003'),
(3, 'Supplies', 300.00, 'Cleaning supplies', 'U003'),
(4, 'Staff Training', '150.00', 'Training session', 'U003'),
(5, 'Marketing', '400.00', 'Advertising campaign', 'U003');

-- LoyaltyPoint (5 rows)
INSERT INTO LoyaltyPoint (user_id, points, level, last_updated, expired_at)
VALUES 
('U001', 100, 'Member', '2025-06-25', '2026-06-25'),
('U002', 200, 'Silver', '2025-06-25', '2026-06-25'),
('U003', 500, 'Gold', '2025-06-25', '2026-06-25'),
('U004', 1000, 'VIP', '2025-06-25', '2026-06-25'),
('U005', 300, 'Silver', '2025-06-25', '2026-06-25');

-- PointTransaction (5 rows)
INSERT INTO PointTransaction (user_id, change_type, points_changed, reason)
VALUES 
('U001', 'Earn', 100, 'Booking completed'),
('U002', 'Redeem', -50, 'Voucher redemption'),
('U003', 'Earn', 200, 'Promotion bonus'),
('U004', 'Adjustment', -100, 'Points correction'),
('U005', 'Earn', 150, 'Referral bonus');

-- PointRedeemVoucher (5 rows)
INSERT INTO PointRedeemVoucher (user_id, voucher_id, points_used, redeemed_at, expired_at)
VALUES 
('U001', 1, 50, '2025-07-01', '2026-07-01'),
('U002', 2, 100, '2025-07-05', '2026-07-05'),
('U003', 3, 150, '2025-07-10', '2026-07-10'),
('U004', 4, 200, '2025-07-15', '2026-07-15'),
('U005', 5, 100, '2025-07-20', '2026-07-20');

-- ChatAIHistory (5 rows)
INSERT INTO ChatAIHistory (user_id, message, created_at)
VALUES 
('U001', 'What are the best rooms?', '2025-06-25 10:00:00'),
('U002', 'Can I book a spa session?', '2025-06-25 10:15:00'),
('U003', 'How to cancel a booking?', '2025-06-25 10:30:00'),
('U004', 'What’s the refund policy?', '2025-06-25 10:45:00'),
('U005', 'Any promotions available?', '2025-06-25 11:00:00');

-- MemberTierHistory (5 rows)
INSERT INTO MemberTierHistory (user_id, old_level, new_level, changed_at, reason)
VALUES 
('U001', NULL, 'Member', '2025-06-25', 'Account created'),
('U002', 'Member', 'Silver', '2025-06-25', 'Points threshold reached'),
('U003', 'Silver', 'Gold', '2025-06-25', 'Frequent bookings'),
('U004', 'Gold', 'VIP', '2025-06-25', 'High spending'),
('U005', 'Member', 'Silver', '2025-06-25', 'Referral program');

-- BackupHistory (5 rows)
INSERT INTO BackupHistory (backup_time, backup_type, backup_path, file_size_mb)
VALUES 
('2025-06-25 01:00:00', 'FULL', '/backups/db_full_20250625.bak', 500.0),
('2025-06-25 02:00:00', 'PARTIAL', '/backups/db_partial_20250625.bak', 100.0),
('2025-06-25 03:00:00', 'FULL', '/backups/db_full_20250625_2.bak', 510.0),
('2025-06-25 04:00:00', 'PARTIAL', '/backups/db_partial_20250625_2.bak', 120.0),
('2025-06-25 05:00:00', 'FULL', '/backups/db_full_20250625_3.bak', 520.0);

-- Permission (5 rows)
INSERT INTO Permission (role, resource, action, allowed)
VALUES 
('Customer', 'Booking', 'Create', 1),
('Staff', 'Booking', 'Read', 1),
('Manager', 'Booking', 'Update', 1),
('Admin', 'UserAccount', 'Delete', 1),
('HotelOwner', 'HotelBranch', 'Update', 1);

-- Notification (5 rows)
INSERT INTO Notification (user_id, title, message, type, related_booking_id, status)
VALUES 
('U001', 'Booking Confirmed', 'Your booking is confirmed', 'Booking', 1, 'Unread'),
('U002', 'New Promotion', 'Summer sale now live!', 'Promotion', NULL, 'Read'),
('U003', 'Payment Received', 'Payment for booking completed', 'Payment', 2, 'Unread'),
('U004', 'Feedback Requested', 'Please review your experience', 'Feedback', 3, 'Unread'),
('U005', 'Tier Upgraded', 'Congratulations on Silver status!', 'TierUpgrade', NULL, 'Read');

-- CartRoomType (5 rows)
INSERT INTO CartRoomType (user_id, room_type_id, quantity, added_at)
VALUES 
('U001', 1, 1, '2025-06-01 10:00:00'),
('U001', 2, 2, '2025-06-02 10:00:00'),
('U001', 3, 1, '2025-06-03 10:00:00'),
('U001', 4, 2, '2025-06-04 10:00:00'),
('U004', 5, 1, '2025-06-05 10:00:00');

-- BenefitRank (5 rows)
INSERT INTO BenefitRank (level, point_rate, discount_percent, benefit)
VALUES 
('Member', 1.00, 5, 'Basic discounts'),
('Silver', 2, 10.00, 'Priority booking'),
('Gold', 5, 15.00, 'Free upgrades'),
('VIP', 10.00, 20, 'Exclusive perks');
GO

INSERT INTO VoucherRedemptionRule (voucher_id, required_points, required_tier, is_active)
VALUES 
(1, 100, NULL, 1),
(2, 200, 'Member', 1),
(3, 250, 'Silver', 1),
(4, 500, 'Gold', 1),
(5, 150, NULL, 1),
(6, 1750, 'VIP', 1);
GO