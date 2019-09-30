package ir.s_tag.musicplayer.DataModel;

public class SongsDataModel {
    private String title;
    private String dir;
    private String author;
    private String cover;
    private int id;

    public  SongsDataModel(){

    }
    public SongsDataModel(int id ,String title, String dir , String author , String cover) {
        this.id = id;
        this.title = title;
        this.dir = dir;
        this.author = author;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
