package Model;

public class Room {

    private int id;
    private String roomNumber;
    private int branchId;
    private int roomTypeId;
    private String status;
    private String imageUrl;
    private String roomTypeName;
    private String hotelName;
    private boolean isDeleted;

    public Room() {
    }

    public Room(String roomNumber, int branchId, int roomTypeId, String status, String imageUrl) {
        this.roomNumber = roomNumber;
        this.branchId = branchId;
        this.roomTypeId = roomTypeId;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public Room(int id, String roomNumber, int branchId, int roomTypeId, String status, String imageUrl) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.branchId = branchId;
        this.roomTypeId = roomTypeId;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    private RoomType roomType;

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
// author : Hung

    @Override
    public String toString() {
        return "Room{"
                + "id=" + id
                + ", roomNumber='" + roomNumber + '\''
                + ", branchId=" + branchId
                + ", roomTypeId=" + roomTypeId
                + ", status='" + status + '\''
                + ", imageUrl='" + imageUrl + '\''
                + '}';
    }
}
