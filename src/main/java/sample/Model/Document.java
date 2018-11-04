package sample.Model;

public class Document {
    private String id;
    private String date;
    private String title;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void print(){
        System.out.println("id "+ id );
        System.out.println("title "+title);
        System.out.println("date "+date);
        System.out.println("Text " +content);
        System.out.println("-------------------------------------------------");
    }
}
