package example;

public class FbData extends FbMsg {

    private String text;

    public FbData(FbEvents event, String text) {
        super(event);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
