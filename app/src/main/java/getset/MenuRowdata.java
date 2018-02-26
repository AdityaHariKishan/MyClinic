package getset;

/**
 * Created by subhashsanghani on 1/15/17.
 */

public class MenuRowdata {
    public int drawable;
    public String title;
    public MenuRowdata(String title, int drawable){
        this.drawable = drawable;
        this.title = title;
    }
    public int getDrawable(){ return  drawable; }
    public String getTitle(){ return title; }

    public void setDrawable(int drawable){ this.drawable = drawable; }
    public void setTitle(String title){ this.title = title; }
}
