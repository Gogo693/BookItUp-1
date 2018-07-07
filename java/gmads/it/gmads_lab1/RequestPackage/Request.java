package gmads.it.gmads_lab1.RequestPackage;



public class Request {

    private String rId;
    private int reviewStatusOwner;
    private int reviewStatusRenter;
    private int requestStatus;
    private String ownerId;
    private String bId;
    private String bName;
    private String renterId;
    private String ownerName;
    private String renterName;
    private String urlBookImage;
    private Long objectID;

    public Request(){}

    public Request(String rId,
                   int reviewStatusOwner,
                   int reviewStatusRenter,
                   int requestStatus,
                   String ownerId,
                   String bId,
                   String bName,
                   String renterId,
                   String ownerName,
                   String renterName,
                   String urlBookImage,
                   Long objectID) {

        this.rId = rId;
        this.reviewStatusOwner = reviewStatusOwner;
        this.reviewStatusRenter = reviewStatusRenter;
        this.requestStatus = requestStatus;
        this.ownerId = ownerId;
        this.bId = bId;
        this.bName = bName;
        this.renterId = renterId;
        this.ownerName = ownerName;
        this.renterName = renterName;
        this.urlBookImage = urlBookImage;
        this.objectID = objectID;
    }

    public Long getObjectID() {
        return objectID;
    }

    public void setObjectID(Long objectID) {
        this.objectID = objectID;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public int getReviewStatusOwner() {
        return reviewStatusOwner;
    }

    public void setReviewStatusOwner(int reviewStatusOwner) {
        this.reviewStatusOwner = reviewStatusOwner;
    }

    public int getReviewStatusRenter() {
        return reviewStatusRenter;
    }

    public void setReviewStatusRenter(int reviewStatusRenter) {
        this.reviewStatusRenter = reviewStatusRenter;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRenterId() {
        return renterId;
    }

    public void setRenterId(String renterId) {
        this.renterId = renterId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getUrlBookImage() {
        return urlBookImage;
    }

    public void setUrlBookImage(String urlBookImage) {
        this.urlBookImage = urlBookImage;
    }
}
