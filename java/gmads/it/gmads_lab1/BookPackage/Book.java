package gmads.it.gmads_lab1.BookPackage;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmads.it.gmads_lab1.UserPackage.Geoloc;

public class Book implements Serializable{


    private String bId;
    private String isbn;
    private String title;
    private String description;
    private String urlimage;
    private String publishDate;
    private List<String> author;
    private List<String> categories;//
    private double avgRating;
    private int nRates;
    private double sumRates;
    private String publisher;
    private List<String> comments;
    private String condition;
    private String owner;
    private String holder;
    private List<Bitmap> images;
    private String indirizzo;
    private Geoloc _geoloc;
    private Double finderLat;
    private String nomeproprietario;
    private List<String> linkrequest;
    private int stato;
    private Long objectID;
    private long distance;

    public Long getObjectID() {
        return objectID;
    }

    public void setObjectID(Long objectID) {
        this.objectID = objectID;
    }

    private static final long serialVersionUID = 1L;

    public int getStato() {
        return stato;
    }

    public void setStato(int stato) {
        this.stato = stato;
    }

    public List<String> getLinkrequest() {
        return linkrequest;
    }

    public void setLinkrequest( List<String> linkrequest ) {
        this.linkrequest = linkrequest;
    }
    public String getNomeproprietario() {
        return nomeproprietario;
    }

    public void setNomeproprietario( String nomeproprietario ) {
        this.nomeproprietario = nomeproprietario;
    }


    public Double getFinderLat() {
        return finderLat;
    }

    public void setFinderLat( Double finderLat ) {
        this.finderLat = finderLat;
    }

    public Double getFinderLng() {
        return finderLng;
    }

    public void setFinderLng( Double finderLng ) {
        this.finderLng = finderLng;
    }

    private Double finderLng;
    public long getDistance() {
        return distance;
    }

    public void setDistance( long distance ) {
        this.distance = distance;
    }


    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor( List<String> author ) {
        this.author = author;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories( List<String> categories ) {
        this.categories = categories;
    }

    public Map<String, String> getNotes() {
        return notes;
    }

    public void setNotes( Map<String, String> notes ) {
        this.notes = notes;
    }

    private Map<String,String> notes=new  HashMap<>();

    public Geoloc get_geoloc() {
        return _geoloc;
    }

    public void set_geoloc(Geoloc _geoloc) {
        this._geoloc = _geoloc;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo( String indirizzo ) {
        this.indirizzo = indirizzo;
    }

    public Book( String BId, String isbn, String title, String description, String urlimage, String publishDate, List<String> author, List<String> categories, String publisher, String owner, Double lat, Double lng, int stato) {

        this.bId = bId;
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.urlimage = urlimage;
        this.publishDate = publishDate;
        this.author = author;
        this.categories = categories;
        this.publisher = publisher;
        this.owner = owner;
        this._geoloc = new Geoloc(lat, lng);
        this.stato = stato;
        comments= Collections.emptyList();
        images=Collections.emptyList();
        notes= Collections.emptyMap();
        linkrequest= new ArrayList<>();
        avgRating=0;
        nRates=0;
        sumRates=0;
        this.holder = owner;

    }

    public Book() {
    }

    public List<Bitmap> getImages() {
        return images;
    }

    public void setImages(List<Bitmap> images) {
        this.images = images;
    }

    public String getBId() {
        return bId;
    }

    public void setBId(String BId) {
        this.bId = BId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlimage() {
        return urlimage;
    }

    public void setUrlimage(String urlimage) {
        this.urlimage = urlimage;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }



    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getnRates() {
        return nRates;
    }

    public void setnRates(int nRates) {
        this.nRates = nRates;
    }

    public double getSumRates() {
        return sumRates;
    }

    public void setSumRates(double sumRates) {
        this.sumRates = sumRates;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) { this.owner = owner; }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public void setfinder( Double finderlat, Double finderlng ) {
        this.finderLat=finderlat;
        this.finderLng=finderlng;
    }
}
