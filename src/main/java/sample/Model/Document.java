package sample.Model;

public class Document {
    private String id;
    private String date;
    private String title;
    private String content;
    private String city;

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getContent() {
        return content;
    }

    void setContent(String content) {
        this.content = content;
    }

    public void setCity(String city){
        this.city=city;
    }

    public String getCity(){
        return city;
    }

    public void print() {
        System.out.println("id :" + id);
        System.out.println("title :" + title);
        System.out.println("date :" + date);
        System.out.println("city :" + city);
        System.out.println("Text :" + content);
        System.out.println("-------------------------------------------------");
    }
}
