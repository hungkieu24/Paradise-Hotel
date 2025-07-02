package Model;

/**
 *
 * @author thien
 */
public class UserAccount {

    private String id;
    private String username;
    private String fullname;
    private String password;
    private String email;
    private String avatar_url;
    private String role;
    private String status;
    private String create_at;
    private String phonenumber;
    private Integer branchId;
    private String branchName;
    private String login_type;
    private boolean is_deleted;
    private String last_login_at;
    private String rank;
  
    public UserAccount() {
    }

    public UserAccount(String id, String username, String password, String email, String avatar_url, String role, String status, String create_at) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.avatar_url = avatar_url;
        this.role = role;
        this.status = status;
        this.create_at = create_at;
    }

    public UserAccount(String id, String username, String password, String email, String avatar_url, String role, String status, String create_at, String phonenumber) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.avatar_url = avatar_url;
        this.role = role;
        this.status = status;
        this.create_at = create_at;
        this.phonenumber = phonenumber;
    }

    // Thêm constructor mới có branchId
    public UserAccount(String id, String username, String password, String email, String avatar_url, String role, String status, String create_at, String phonenumber, Integer branchId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.avatar_url = avatar_url;
        this.role = role;
        this.status = status;
        this.create_at = create_at;
        this.phonenumber = phonenumber;
        this.branchId = branchId;
    }

    //author: hung
    //Content: Constructor đầy đủ cho data mới
    public UserAccount(String id, String username, String password, String email, String avatar_url, String role, String status, String create_at, String phonenumber, Integer branchId, String branchName, String fullname, String login_type, boolean is_deleted, String last_login_at) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.avatar_url = avatar_url;
        this.role = role;
        this.status = status;
        this.create_at = create_at;
        this.phonenumber = phonenumber;
        this.branchId = branchId;
        this.branchName = branchName;
        this.fullname = fullname;
        this.login_type = login_type;
        this.is_deleted = is_deleted;
        this.last_login_at = last_login_at;
    }

    //author: hung
    //Content: Constructor dùng để add staff, manager
    public UserAccount(String username, String password, String email, String avatar_url, String role, String phonenumber, Integer branchId, String fullname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.avatar_url = avatar_url;
        this.role = role;
        this.phonenumber = phonenumber;
        this.branchId = branchId;
        this.fullname = fullname;
    }
    
    
    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    // Getter và Setter cho branchId
    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(Integer branchId) {
        this.branchId = branchId;
    }

    // Getter và Setter cho rank
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
  
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getLast_login_at() {
        return last_login_at;
    }

    public void setLast_login_at(String last_login_at) {
        this.last_login_at = last_login_at;
    }

    @Override
    public String toString() {
        return "UserAccount{" + "id=" + id + ", username=" + username + ", password=" + password + ", email=" + email + ", avatar_url=" + avatar_url + ", role=" + role + ", status=" + status + ", create_at=" + create_at + ", phonenumber=" + phonenumber + ", branchId=" + branchId + ", branchName=" + branchName + ", fullname=" + fullname + ", login_type=" + login_type + ", is_deleted=" + is_deleted + ", last_login_at=" + last_login_at + '}';
    }
}
