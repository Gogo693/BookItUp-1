package gmads.it.gmads_lab1.RequestPackage;

public class ReferenceRequest {

    private String bookname;
    private String imgurl;
    private String nomerichiedente;
    private String requestid;
    private String bookid;

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getNomerichiedente() {
        return nomerichiedente;
    }

    public void setNomerichiedente(String nomerichiedente) {
        this.nomerichiedente = nomerichiedente;
    }

    public String getRequestid() {
        return requestid;
    }

    public void setRequestid(String requestid) {
        this.requestid = requestid;
    }

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public ReferenceRequest(String bookname, String imgurl, String nomerichiedente, String requestid, String bookid) {
        this.bookname = bookname;
        this.imgurl = imgurl;
        this.nomerichiedente = nomerichiedente;
        this.requestid = requestid;
        this.bookid = bookid;
    }

    public ReferenceRequest(){

    }
}
