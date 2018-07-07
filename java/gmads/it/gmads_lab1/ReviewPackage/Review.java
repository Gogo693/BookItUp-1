package gmads.it.gmads_lab1.ReviewPackage;

public class Review {
    private String user;
    private String userid;
    private String comment;
    private float rate;

    public Review( String user, String userid, String comment, float rate ) {
        this.user = user;
        this.userid = userid;
        this.comment = comment;
        this.rate = rate;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid( String userid ) {
        this.userid = userid;
    }

    public Review() {

    }

    public String getUser() {
        return user;
    }

    public void setUser( String user ) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = comment;
    }

    public float getRate() {
        return rate;
    }

    public void setRate( float rate ) {
        this.rate = rate;
    }
}
