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
	branch_id INT NOT NULL,
    is_deleted BIT DEFAULT 0
);

CREATE TABLE RoomAmenity (
    room_type_id INT,
    amenity_id INT,
    PRIMARY KEY (room_type_id, amenity_id)
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
    expense_type NVARCHAR(50),
    amount DECIMAL(18,2) NOT NULL,
    description NVARCHAR(MAX),
    expense_date DATE DEFAULT GETDATE(),
    created_by VARCHAR(10)
);

-- 9. LOYALTY POINT & REDEEM
CREATE TABLE LoyaltyPoint (
    user_id VARCHAR(10) PRIMARY KEY,
    points INT,
    level VARCHAR(10) CHECK (level IN ('Member', 'Silver', 'Gold', 'VIP')) DEFAULT 'Member',
    last_updated DATETIME DEFAULT GETDATE(),
    expired_at DATETIME,
	total_spending DECIMAL(18,2) DEFAULT 0,
    lifetime_points INT DEFAULT 0,
    points_used INT DEFAULT 0,
    last_tier_check DATETIME DEFAULT GETDATE(),
    next_tier_spending_needed DECIMAL(18,2) DEFAULT 0
);

CREATE TABLE PointTransaction (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10),
    change_type VARCHAR(20) CHECK (change_type IN ('Earn', 'Redeem', 'Change' , 'Adjustment')),
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
    violation VARCHAR(MAX)
);

CREATE TABLE MemberTierHistory (
    id INT PRIMARY KEY IDENTITY(1, 1),
    user_id VARCHAR(10),
    old_level VARCHAR(10) CHECK (old_level IN ('Member', 'Silver', 'Gold', 'VIP')),
    new_level VARCHAR(10) CHECK (new_level IN ('Member', 'Silver', 'Gold', 'VIP')),
    changed_at DATETIME DEFAULT GETDATE(),
    reason NVARCHAR(MAX)
);

CREATE TABLE MemberTierRule (
    id INT PRIMARY KEY IDENTITY(1,1),
    level VARCHAR(10) CHECK (level IN ('Member', 'Silver', 'Gold', 'VIP')) NOT NULL UNIQUE,
    min_spending DECIMAL(18,2) NOT NULL,
    description NVARCHAR(255)
);

CREATE TABLE BackupHistory (
    id INT PRIMARY KEY IDENTITY(1,1),
    backup_time DATETIME DEFAULT GETDATE(),
    backup_type VARCHAR(20),
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

CREATE TABLE BranchMonthlyReport (
    Id INT IDENTITY PRIMARY KEY,
    BranchId INT NOT NULL,
    ReportMonth DATE NOT NULL,
    Revenue DECIMAL(18, 2) NOT NULL,
    Expenses DECIMAL(18, 2) NOT NULL,
    Profit DECIMAL(18, 2) NOT NULL,       -- Do code tính: Revenue - Expenses
    ProfitRate DECIMAL(5, 2) NOT NULL,    -- Do code tính: (Profit / Capital) * 100
    FilePath NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE InitialInvestment (
	id INT PRIMARY KEY IDENTITY(1,1),
    BranchId INT NOT NULL,
    Capital DECIMAL(18, 2) NOT NULL,
    InvestedDate DATE NOT NULL DEFAULT GETDATE()
);

CREATE TABLE FeedbackComment (
    id INT PRIMARY KEY IDENTITY(1,1),
    feedback_id INT NOT NULL,
    parent_comment_id INT NULL,
    user_id VARCHAR(10) NOT NULL,
    content NVARCHAR(MAX) NOT NULL,
    image_url NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    is_edited BIT DEFAULT 0,
    is_deleted BIT DEFAULT 0,
    FOREIGN KEY (feedback_id) REFERENCES Feedback(id),
    FOREIGN KEY (parent_comment_id) REFERENCES FeedbackComment(id)
);

CREATE TABLE Revenue (
    id INT PRIMARY KEY IDENTITY(1,1),
    branch_id INT NOT NULL,
    revenue_type NVARCHAR(100),         -- Ví dụ: "Online Booking", "Tiền mặt", "Dịch vụ spa"
    amount DECIMAL(18,2) NOT NULL,
    revenue_date DATE DEFAULT GETDATE(),
    source VARCHAR(20) NOT NULL,       -- "SYSTEM" hoặc "MANUAL"
    description NVARCHAR(MAX),         -- Ghi chú thêm nếu có
    created_by VARCHAR(10),            -- ID người tạo bản ghi
    created_at DATETIME DEFAULT GETDATE()
);

-- 13. FOREIGN KEYS
ALTER TABLE UserAccount ADD CONSTRAINT FK_UserAccount_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE HotelBranch ADD CONSTRAINT FK_HotelBranch_Owner FOREIGN KEY (owner_id) REFERENCES UserAccount (id);
ALTER TABLE HotelBranch ADD CONSTRAINT FK_HotelBranch_Manager FOREIGN KEY (manager_id) REFERENCES UserAccount (id);
ALTER TABLE RoomType ADD CONSTRAINT FK_RoomType_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE Room ADD CONSTRAINT FK_Room_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch (id);
ALTER TABLE Room ADD CONSTRAINT FK_Room_RoomType FOREIGN KEY (room_type_id) REFERENCES RoomType (id);
ALTER TABLE RoomAmenity ADD CONSTRAINT FK_RoomAmenity_RoomType FOREIGN KEY (room_type_id) REFERENCES RoomType(id);
ALTER TABLE RoomAmenity ADD CONSTRAINT FK_RoomAmenity_Amenity FOREIGN KEY (amenity_id) REFERENCES Amenity(id);
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
ALTER TABLE BranchMonthlyReport ADD CONSTRAINT FK_BranchMonthlyReport_Branch FOREIGN KEY (BranchId) REFERENCES HotelBranch(Id);
ALTER TABLE InitialInvestment ADD CONSTRAINT FK_InitialInvestment_Branch FOREIGN KEY (BranchId) REFERENCES HotelBranch(id);
ALTER TABLE Revenue ADD CONSTRAINT FK_Revenue_Branch FOREIGN KEY (branch_id) REFERENCES HotelBranch(id);
ALTER TABLE Booking ADD exported_to_revenue BIT DEFAULT 0;
ALTER TABLE BenefitRank ADD CONSTRAINT FK_BenefitRank_TierRule FOREIGN KEY (level) REFERENCES MemberTierRule(level);
ALTER TABLE VNPayTransaction ADD vnp_PayDate VARCHAR(20);
ALTER TABLE ChatAIHistory ADD response NVARCHAR(MAX);
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
('U002', 'jane_smith', 'hashed_password2', N'Jane Smith', 'jane.smith@email.com', 'Local', 
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
('Standard', 'Cozy room with basic amenities', 500000.00, 2, 1, 1, 'img/room1.jpg'),
('Deluxe', 'Spacious room with sea view', 1000000.00, 3, 2, 2, 'img/room2.jpg'),
('Suite', 'Luxury suite with balcony', 2000000.00, 4, 2, 3, 'img/room3.jpg'),
('Family', 'Large room for families', 1500000.00, 4, 3, 4, 'img/room4.jpg'),
('Single', 'Compact room for solo travelers', 400000.00, 1, 0, 5, 'img/room5.jpg');

-- Room (5 rows)
-- Thêm phòng cho RoomType 1 (Standard) tại chi nhánh 1 (Hanoi)
INSERT INTO Room (room_number, branch_id, room_type_id, status, image_url) VALUES 
('101', 1, 1, 'Available', 'room101.jpg'),
('102', 2, 2, 'Booked', 'room102.jpg'),
('103', 1, 1, 'Available', 'room103.jpg'),
('104', 1, 1, 'Available', 'room104.jpg'),
('105', 1, 1, 'Available', 'room105.jpg'),
('106', 1, 1, 'Available', 'room105.jpg'),
('107', 1, 1, 'Available', 'room105.jpg'),
('108', 1, 1, 'Available', 'room105.jpg'),
('201', 3, 3, 'Occupied', 'room201.jpg'),
('202', 1, 2, 'Available', 'room202.jpg'),
('203', 1, 2, 'Available', 'room203.jpg'),
('204', 1, 2, 'Available', 'room204.jpg'),
('205', 1, 2, 'Available', 'room204.jpg'),
('206', 1, 2, 'Available', 'room204.jpg'),
('207', 1, 2, 'Available', 'room204.jpg'),
('301', 4, 4, 'Available', 'room301.jpg'),
('401', 5, 5, 'Maintenance', 'room401.jpg');
Go

-- Amenity (5 rows)
INSERT INTO Amenity (name, description, branch_id)
VALUES 
('WiFi', 'High-speed internet access', 1),
('Air Conditioning', 'Climate control unit', 1),
('Mini Bar', 'Refrigerated mini bar', 1),
('TV', 'Flat-screen television', 1),
('Safe', 'In-room safety deposit box', 1);

-- RoomAmenity (5 rows)
INSERT INTO RoomAmenity (room_type_id, amenity_id)
VALUES 
(1, 1), (1, 2), (2, 3), (3, 4), (4, 5);

-- Service (5 rows)
INSERT INTO Service (name, description, price, branch_id, status, image_url)
VALUES 
('Breakfast', 'Buffet breakfast', 150000.00, 1, 'Active', 'img/breakfast.jpg'),
('Spa', 'Relaxing spa treatment', 500000.00, 1, 'Active', 'img/spa.jpg'),
('Laundry', 'Same-day laundry service', 100000.00, 2, 'Active', 'img/laundry.jpg'),
('Airport Shuttle', 'Transportation to airport', 200000.00, 3, 'Inactive', 'img/shuttle.jpg'),
('Room Service', '24/7 room service', 250000.00, 4, 'Active', 'img/roomservice.jpg'),
('Gym Access', 'Unlimited gym access', 300000.00, 1, 'Active', 'img/gym.jpg'),
('Swimming Pool', 'All-day pool access', 200000.00, 1, 'Active', 'img/swim.jpg'),
('Massage', 'Relaxing massage session', 400000.00, 2, 'Active', 'img/massage.jpg'),
('Car Rental', 'Daily car rental service', 600000.00, 2, 'Active', 'img/car.jpg'),
('Tour Guide', 'City tour guide service', 350000.00, 2, 'Active', 'img/tour.jpg'),
('Conference Room', 'Spacious conference room rental', 1000000.00, 3, 'Active', 'img/conference.jpg'),
('Private Dining', 'Private dining room service', 700000.00, 3, 'Active', 'img/privateDining.jpg'),
('Babysitting', 'Babysitting service', 450000.00, 3, 'Active', 'img/babySitting.jpg'),
('Valet Parking', 'Valet parking service', 150000.00, 3, 'Active', 'img/valet.jpg'),
('Sauna', 'Relaxing sauna session', 250000.00, 4, 'Active', 'img/sauna.jpg'),
('Pet Care', 'Pet care during stay', 200000.00, 4, 'Active', 'img/pet.jpg'),
('Yoga Class', 'Daily yoga classes', 180000.00, 4, 'Active', 'img/yoga.jpg'),
('Breakfast Buffet', 'Delicious morning buffet', 220000.00, 5, 'Active', 'img/buffet.jpg'),
('Cooking Class', 'Learn local dishes', 500000.00, 5, 'Active', 'img/cooking.jpg'),
('Wine Tasting', 'Local wine tasting experience', 550000.00, 5, 'Active', 'img/wine.jpg'),
('City Transfer', 'Private transfer around city', 350000.00, 5, 'Active', 'img/city.jpg');

-- Bảng Booking (đã sửa)
INSERT INTO Booking (user_id, created_by, booking_time, check_in, check_out, status, total_price, payment_status, branch_id, note, cancel_time)
VALUES 
('U001', NULL, '2025-06-28 10:00:00', '2025-07-01 14:00:00', '2025-07-03 12:00:00', 'NoShow', 100.00, 'Unpaid', 1, 'Early check-in requested', NULL),
('U001', 'U002', '2025-07-02 09:00:00', '2025-07-05 14:00:00', '2025-07-07 12:00:00', 'Completed', 200.00, 'Paid', 1, NULL, NULL),
('U001', NULL, '2025-07-07 10:00:00', '2025-07-10 14:00:00', '2025-07-12 12:00:00', 'Completed', 150.00, 'Paid', 2, 'Extra pillows', NULL),
('U001', NULL, '2025-07-12 11:00:00', '2025-07-15 14:00:00', '2025-07-17 12:00:00', 'Completed', 300.00, 'Paid', 2, NULL, NULL),
('U001', 'U002', '2025-07-18 14:00:00', '2025-07-20 14:00:00', '2025-07-22 12:00:00', 'CheckedIn', 120.00, 'Paid', 3, 'Late checkout', NULL),
('U001', NULL, '2025-07-20 10:00:00', '2025-07-25 14:00:00', '2025-07-27 12:00:00', 'Cancelled', 80.00, 'Unpaid', 3, 'Cancelled due to schedule change', '2025-07-20 12:00:00'),
('U001', NULL, '2025-07-20 11:00:00', '2025-08-01 14:00:00', '2025-08-03 12:00:00', 'Pending', 90.00, 'Unpaid', 4, NULL, NULL),
('U001', 'U002', '2025-07-20 12:00:00', '2025-08-05 14:00:00', '2025-08-07 12:00:00', 'Pending', 110.00, 'Unpaid', 4, 'Payment pending', NULL),
('U001', NULL, '2025-07-20 13:00:00', '2025-08-10 14:00:00', '2025-08-12 12:00:00', 'Paid', 130.00, 'Paid', 1, 'Special request for view', NULL),
('U001', NULL, '2025-07-20 14:00:00', '2025-08-15 14:00:00', '2025-08-17 12:00:00', 'Paid', 250.00, 'Paid', 2, NULL, NULL),
('U003', NULL, '2025-07-15 10:00:00', '2025-07-18 14:00:00', '2025-07-20 12:00:00', 'Completed', 300.00, 'Paid', 1, 'Already checked in', NULL),
('U004', NULL, '2025-07-16 10:00:00', '2025-07-19 14:00:00', '2025-07-21 12:00:00', 'CheckedIn', 400.00, 'Paid', 1, 'Paid but not assigned room yet', NULL),
('U005', NULL, '2025-07-16 11:00:00', '2025-07-19 14:00:00', '2025-07-21 12:00:00', 'Cancelled', 250.00, 'Unpaid', 1, 'Waiting for payment', '2025-07-20 15:48:00'),
('U006', NULL, '2025-07-16 12:00:00', '2025-07-19 14:00:00', '2025-07-20 12:00:00', 'Completed', 600.00, 'Paid', 1, 'Booked multiple rooms', NULL),
('U007', NULL, '2025-07-16 13:00:00', '2025-07-19 12:00:00', '2025-07-21 12:00:00', 'CheckedIn', 350.00, 'Paid', 1, 'Checked in early', NULL),
('U008', NULL, '2025-07-16 14:00:00', '2025-07-19 14:00:00', '2025-07-21 12:00:00', 'CheckedIn', 500.00, 'Paid', 1, 'Staying from yesterday', NULL),
('U007', NULL, '2025-07-18 10:00:00', '2025-07-20 14:00:00', '2025-07-22 12:00:00', 'CheckedIn', 700.00, 'Paid', 1, 'Booking paid today', NULL),
('U006', NULL, '2025-07-18 11:00:00', '2025-07-20 14:00:00', '2025-07-21 12:00:00', 'CheckedIn', 900.00, 'Paid', 1, 'Booked multiple room types', NULL),
('U005', NULL, '2025-07-18 12:00:00', '2025-07-20 10:00:00', '2025-07-21 12:00:00', 'CheckedIn', 600.00, 'Paid', 1, 'Checked in this morning', NULL),
('U004', NULL, '2025-07-18 13:00:00', '2025-07-20 14:00:00', '2025-07-21 12:00:00', 'Cancelled', 300.00, 'Unpaid', 1, 'Waiting for payment', '2025-07-20 15:48:00');

-- Bảng BookingRoomType (giữ nguyên)
INSERT INTO BookingRoomType (booking_id, room_type_id, quantity, price_per_room)
VALUES 
(1, 1, 1, 500000.00),
(2, 2, 1, 1000000.00),
(3, 3, 1, 1500000.00),
(4, 4, 2, 1500000.00),
(5, 5, 1, 400000.00),
(11, 1, 1, 300000.00),
(12, 2, 1, 400000.00),
(13, 3, 1, 250000.00),
(14, 1, 2, 200000.00),
(14, 3, 1, 200000.00),
(15, 2, 1, 350000.00),
(16, 1, 1, 500000.00),
(17, 2, 1, 700000.00),
(18, 1, 2, 300000.00),
(18, 3, 1, 300000.00),
(19, 2, 1, 600000.00),
(20, 3, 1, 300000.00);

-- Bảng RoomAssignment (giữ nguyên)
INSERT INTO RoomAssignment (booking_id, room_id, assigned_at)
VALUES 
(1, 1, '2025-07-01 14:00:00'),
(2, 2, '2025-07-05 14:00:00'),
(3, 3, '2025-07-10 14:00:00'),
(4, 4, '2025-07-15 14:00:00'),
(5, 5, '2025-07-20 14:00:00'),
(11, 3, '2025-07-18 14:00:00'),
(12, 12, '2025-07-19 14:00:00'), -- Thêm cho booking ID 12
(15, 10, '2025-07-19 12:00:00'),
(16, 4, '2025-07-19 14:00:00'),
(17, 13, '2025-07-20 14:00:00'), -- Thêm cho booking ID 17
(18, 14, '2025-07-20 14:00:00'), -- Thêm cho booking ID 18
(18, 15, '2025-07-20 14:00:00'), -- Thêm cho booking ID 18
(18, 16, '2025-07-20 14:00:00'), -- Thêm cho booking ID 18
(19, 11, '2025-07-20 10:00:00');
-- Voucher (5 rows)
INSERT INTO Voucher (code, description, discount_percent, discount_amount, min_price, total_quantity, used_quantity, branch_id, valid_from, valid_to, status)
VALUES 
('DISC10', '10% off for first booking', 10, NULL, 500000.00, 100, 10, 1, '2025-06-01', '2025-12-31', 'Active'),
('SAVE20', '20 USD off', NULL, 200000.00, 1000000.00, 50, 5, 1, '2025-06-01', '2025-12-31', 'Active'),
('SUMMER25', 'Summer discount', 25, NULL, 1500000.00, 200, 20, 2, '2025-06-01', '2025-08-31', 'Active'),
('VIP50', 'VIP discount', NULL, 500000.00, 2000000.00, 30, 2, 3, '2025-06-01', '2025-12-31', 'Active'),
('WELCOME15', 'Welcome offer', 15, NULL, 800000.00, 1500000, 15, 4, '2025-06-01', '2025-12-31', 'Active'),
('VIPONLY70', '70% discount for VIP users only', 70, NULL, 3000000.00, 20, 0, 2, '2025-06-01', '2025-12-31', 'Active');

-- BookingVoucher (5 rows)
INSERT INTO BookingVoucher (booking_id, voucher_id, used_at)
VALUES 
(1, 1, '2025-07-01 10:00:00'),
(2, 2, '2025-07-05 10:00:00'),
(3, 3, '2025-07-10 10:00:00'),
(4, 4, '2025-07-15 10:00:00'),
(5, 5, '2025-07-20 10:00:00');


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
('Winter Deal', 'Winter special offer', NULL, 300000.00, '2025-12-01', '2026-02-28', 2, 2, 'Active'),
('Spring Promo', 'Spring getaway discount', 10.00, NULL, '2025-03-01', '2025-05-31', 3, 3, 'Active'),
('Fall Sale', 'Fall season discount', NULL, 250000.00, '2025-09-01', '2025-11-30', 4, 4, 'Active'),
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
(1, 1000000.00, 'Pending', '2025-07-01 10:00:00'),
(2, 2000000.00, 'Completed', '2025-07-05 10:00:00'),
(3, 1500000.00, 'Completed', '2025-07-10 10:00:00'),
(4, 3000000.00, 'Completed', '2025-07-15 10:00:00'),
(5, 1200000.00, 'Refunded', '2025-07-20 10:00:00');

-- VNPayTransaction (5 rows)
INSERT INTO VNPayTransaction (payment_id, vnp_TxnRef, vnp_TransactionNo, vnp_ResponseCode, vnp_Amount, vnp_BankCode, vnp_CardType, vnp_SecureHash, is_refunded)
VALUES 
(1, 'TXN001', '123456', '00', 1000000.00, 'NCB', 'VISA', 'hash1', 0),
(2, 'TXN002', '123457', '00', 2000000.00, 'VCB', 'MASTER', 'hash2', 0),
(3, 'TXN003', '123458', '00', 1500000.00, 'TPB', 'VISA', 'hash3', 0),
(4, 'TXN004', '123459', '00', 3000000.00, 'MBB', 'MASTER', 'hash4', 0),
(5, 'TXN005', '123460', '07', 1200000.00, 'ACB', 'VISA', 'hash5', 1);

-- Invoice (5 rows)
INSERT INTO Invoice (booking_id, total_amount, issued_at, pdf_url)
VALUES 
(1, 1000000.00, '2025-07-01 10:00:00', 'invoice1.pdf'),
(2, 2000000.00, '2025-07-05 10:00:00', 'invoice2.pdf'),
(3, 1500000.00, '2025-07-10 10:00:00', 'invoice3.pdf'),
(4, 3000000.00,'2025-07-15 10:00:00', 'invoice4.pdf'),
(5, 1200000.00, '2025-07-20 10:00:00', 'invoice5.pdf');

-- Expense (5 rows)
INSERT INTO Expense (branch_id, expense_type, amount, description, created_by)
VALUES 
(1, 'Utilities', 5000000.00, 'Electricity bill', 'U003'),
(2, 'Maintenance', 2000000.00, 'Room repairs', 'U003'),
(3, 'Supplies', 3000000.00, 'Cleaning supplies', 'U003'),
(4, 'Staff Training', 15000000.00, 'Training session', 'U003'),
(5, 'Marketing', 4000000.00, 'Advertising campaign', 'U003');

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

-- 15. INDEXES FOR PERFORMANCE
CREATE INDEX IX_LoyaltyPoint_TotalSpending ON LoyaltyPoint(total_spending);
CREATE INDEX IX_MemberTierRule_MinSpending ON MemberTierRule(min_spending);
CREATE INDEX IX_PointTransaction_UserDate ON PointTransaction(user_id, created_at);
CREATE INDEX IX_Booking_StatusUser ON Booking(status, user_id);
CREATE INDEX IX_Booking_TotalPrice ON Booking(total_price);

-- 16. INSERT INITIAL & SAMPLE DATA
-- Setup the rank thresholds in the new table
INSERT INTO MemberTierRule (level, min_spending, description) VALUES
('Member', 0, N'Hạng thành viên, chi tiêu từ 0 VND'),
('Silver', 5000001, N'Hạng Bạc, chi tiêu từ 5,000,001 VND'),
('Gold', 10000001, N'Hạng Vàng, chi tiêu từ 10,000,001 VND'),
('VIP', 20000001, N'Hạng VIP, chi tiêu trên 20,000,001 VND');

-- BenefitRank Data (FIXED: Removed point_rate)
INSERT INTO BenefitRank (level, discount_percent, benefit) VALUES
('Member', 0, N'Tích điểm cơ bản, không giảm giá'),
('Silver', 5, N'Giảm giá 5%, ưu tiên check-in'),
('Gold', 10, N'Giảm giá 10%, phòng upgrade miễn phí'),
('VIP', 15, N'Giảm giá 15%, dịch vụ VIP, late checkout');

INSERT INTO VoucherRedemptionRule (voucher_id, required_points, required_tier, is_active)
VALUES 
(1, 100, NULL, 1),
(2, 200, 'Member', 1),
(3, 250, 'Silver', 1),
(4, 500, 'Gold', 1),
(5, 150, NULL, 1),
(6, 1750, 'VIP', 1);
GO

INSERT INTO BranchMonthlyReport
(BranchId, ReportMonth, Revenue, Expenses, Profit, ProfitRate, FilePath)
VALUES 
(1, '2025-05-01', 100000000, 60000000, 40000000, 8.00, N'/reports/branch1_2025_05.xlsx'),
(1, '2025-06-01', 120000000, 70000000, 50000000, 10.00, N'/reports/branch1_2025_06.xlsx'),
(2, '2025-05-01', 80000000, 90000000, -10000000, -1.67, N'/reports/branch2_2025_05.xlsx'),
(2, '2025-06-01', 95000000, 70000000, 25000000, 4.17, N'/reports/branch2_2025_06.xlsx'),
(3, '2025-05-01', 75000000, 75000000, 0, 0.00, N'/reports/branch3_2025_05.xlsx'),
(3, '2025-06-01', 90000000, 85000000, 5000000, 0.91, N'/reports/branch3_2025_06.xlsx');
GO

INSERT INTO InitialInvestment (BranchId, Capital, InvestedDate)
VALUES 
(1, 500000000, '2025-05-01'),
(2, 600000000, '2025-05-01'),
(3, 550000000, '2025-05-01');
GO

-- === Feedback ID 1 ===
-- Manager phản hồi
INSERT INTO FeedbackComment (feedback_id, parent_comment_id, user_id, content)
VALUES 
(1, NULL, 'U003', N'Cảm ơn bạn đã tin tưởng và lựa chọn khách sạn! Rất mong được đón tiếp bạn lần sau.'),

-- Giả sử ID dòng trên là 1
-- Khách hàng phản hồi lại
(1, 1, 'U001', N'Chắc chắn rồi, tôi sẽ quay lại!'),

-- === Feedback ID 2 ===
-- Manager phản hồi
(2, NULL, 'U003', N'Cảnh đẹp và dịch vụ tốt là nhờ bạn đấy! Cảm ơn phản hồi tích cực.'),

-- Giả sử ID dòng trên là 2
-- Khách hàng phản hồi lại
(2, 3, 'U001', N'Mình đã giới thiệu cho bạn bè rồi đó!'),

-- === Feedback ID 3 ===
-- Manager phản hồi
(3, NULL, 'U003', N'Cảm ơn góp ý của bạn! Chúng tôi sẽ cân nhắc nâng cấp phòng trong tương lai.'),

-- Giả sử ID dòng trên là 3
-- Khách hàng phản hồi lại
(3, 5, 'U001', N'Mong lần sau có phòng rộng hơn nhé!'),

-- === Feedback ID 4 ===
-- Manager phản hồi
(4, NULL, 'U003', N'Cảm ơn bạn! Chúng tôi sẽ tiếp tục cải thiện để mang lại trải nghiệm tốt hơn.'),

-- Giả sử ID dòng trên là 4
-- Khách hàng phản hồi lại
(4, 7, 'U001', N'Dịch vụ đã tốt rồi, cố gắng duy trì nhé!'),

-- === Feedback ID 5 ===
-- Manager phản hồi
(5, NULL, 'U003', N'Rất vui khi gia đình bạn có kỳ nghỉ tuyệt vời! Hẹn gặp lại trong tương lai gần.'),

-- Giả sử ID dòng trên là 5
-- Khách hàng phản hồi lại
(5, 9, 'U001', N'Bọn trẻ nhà tôi rất thích, cảm ơn khách sạn!');
 GO

  INSERT INTO Revenue (branch_id, revenue_type, amount, revenue_date, source, description, created_by)
VALUES 
(1, 'Online Booking', 2500000, '2025-07-01', 'SYSTEM', null, 'SYSTEM'),
(2, 'Online Booking', 3200000, '2025-07-02', 'SYSTEM', null, 'SYSTEM'),
(1, N'Online Booking', 50000000, '2025-05-10', 'SYSTEM', N'Monthly summary May', 'SYSTEM'),
(1, N'Online Booking', 70000000, '2025-06-05', 'SYSTEM', N'Monthly summary June', 'SYSTEM'),
(2, 'Online Booking', 50000000, '2025-05-07', 'SYSTEM', N'Monthly summary May', 'SYSTEM'),
(2, 'Online Booking',      60000000, '2025-06-03', 'SYSTEM', N'Monthly summary June', 'SYSTEM'),
(3, 'Online Booking', 60000000, '2025-06-06', 'SYSTEM', N'Monthly summary June', 'SYSTEM');

INSERT INTO Expense (branch_id, expense_type, amount, description, expense_date, created_by) VALUES
(1, 'Utilities', 30000000, 'Electricity, water, and internet', '2025-05-10', 'U003'),
(1, 'Maintenance', 30000000, 'AC repair and room maintenance', '2025-05-20', 'U003'),
(1, 'Staff Salary', 40000000, 'Monthly staff salaries', '2025-06-05', 'U003'),
(1, 'Supplies', 30000000, 'Cleaning and room supplies', '2025-06-15', 'U003'),
(2, 'Marketing', 50000000, 'TV and online advertising', '2025-05-12', 'U003'),
(2, 'Utilities', 40000000, 'Power and internet', '2025-05-25', 'U003'),
(2, 'Maintenance', 30000000, 'Plumbing and furniture repair', '2025-06-04', 'U003'),
(2, 'Staff Bonus', 40000000, 'Mid-year performance bonus', '2025-06-18', 'U003'),
(3, 'Supplies', 35000000, 'Bathroom and bedding supplies', '2025-05-08', 'U003'),
(3, 'Staff Salary', 40000000, 'May salaries', '2025-05-28', 'U003'),
(3, 'Marketing', 45000000, 'Social media and event promotion', '2025-06-02', 'U003'),
(3, 'Utilities', 40000000, 'Electricity and water', '2025-06-19', 'U003');

CREATE TABLE Wallet (
    WalletID INT PRIMARY KEY IDENTITY(1,1),
    UserID VARCHAR(10) UNIQUE NOT NULL,
    Balance DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    UpdatedAt DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (UserID) REFERENCES UserAccount(id)
);

-- 🌟 Dữ liệu mẫu:
INSERT INTO Wallet (UserID, Balance)
VALUES 
('U001', 1000000),  -- Khách A có sẵn 2 triệu
('U002', 0),         -- Khách B chưa nạp tiền
('U003', 500000);    -- Khách C có 500k

CREATE TABLE BankAccount (
    BankAccountID INT PRIMARY KEY IDENTITY(1,1),
    UserID VARCHAR(10) NOT NULL,
    BankName NVARCHAR(255) NOT NULL,
    AccountNumber NVARCHAR(100) NOT NULL,
    AccountHolder NVARCHAR(255) NOT NULL,
    IsDefault BIT NOT NULL DEFAULT 0,
    FOREIGN KEY (UserID) REFERENCES UserAccount(id)
);
-- Mỗi user có 1 tài khoản mặc định:
INSERT INTO BankAccount (UserID, BankName, AccountNumber, AccountHolder, IsDefault)
VALUES
('U001', N'Vietcombank', '0123456789', N'John Doe', 1),
('U001', N'Techcombank', '9876543210', N'John Doe', 0),

('U002', N'ACB', '1122334455', N'Jane Smith', 1),

('U003', N'MB Bank', '5566778899', N'Mike Johnson', 1);


CREATE TABLE WalletTransaction (
    TransactionID BIGINT PRIMARY KEY IDENTITY(1,1),
    WalletID INT NOT NULL,
    Amount DECIMAL(18, 2) NOT NULL,
    TransactionType VARCHAR(20) NOT NULL CHECK (
        TransactionType IN ('Deposit', 'Withdraw', 'Payment', 'Refund')
    ),
    Description NVARCHAR(500),
    BookingID INT NULL,
    BranchID INT NULL,
	BankAccountNumber VARCHAR(50) NULL,
    CreatedBy VARCHAR(10),
    Status VARCHAR(20) NOT NULL CHECK (
        Status IN ('Pending', 'Success', 'Failed', 'Cancelled')
    ) DEFAULT 'Pending',
    CreatedAt DATETIME NOT NULL DEFAULT GETDATE(),

    FOREIGN KEY (WalletID) REFERENCES Wallet(WalletID),
    FOREIGN KEY (BookingID) REFERENCES Booking(id),
    FOREIGN KEY (BranchID) REFERENCES HotelBranch(id),
    FOREIGN KEY (CreatedBy) REFERENCES UserAccount(id)
);

-- Giả sử WalletID:
-- WalletID = 1 (UserID = 'U001')
-- WalletID = 2 (UserID = 'U002')
-- WalletID = 3 (UserID = 'U003')

INSERT INTO WalletTransaction 
(WalletID, Amount, TransactionType, Description, BookingID, BranchID,BankAccountNumber, CreatedBy, Status)
VALUES
-- U001: Nạp tiền vào ví
(1, 1000000, 'Deposit', N'Nạp 1 triệu vào ví', NULL, NULL,'0123456789', 'U001', 'Success'),

-- U001: Thanh toán đơn đặt phòng (BookingID = 2, branch_id = 1)
(1, 800000, 'Payment', N'Thanh toán đơn đặt phòng #2', 2, 1,'0123456789', 'U001', 'Success'),

-- U001: Hoàn tiền đơn đã hủy (BookingID = 6, branch_id = 3)
(1, 800000, 'Refund', N'Hoàn tiền do hủy đơn phòng #2', 6, 3,'0123456789', 'U003', 'Success'),

-- U002: Yêu cầu rút tiền
(2, 300000, 'Withdraw', N'Yêu cầu rút tiền về tài khoản ngân hàng', NULL, NULL,'1122334455', 'U002', 'Pending'),

-- U003: Thanh toán thất bại (BookingID = 11, branch_id = 1)
(3, 1000000, 'Payment', N'Thanh toán đơn đặt phòng #11 không thành công', 11, 1,'5566778899', 'U003', 'Failed');
