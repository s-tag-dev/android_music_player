package ir.s_tag.musicplayer.CustomViews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import ir.s_tag.musicplayer.DataModel.Indicator;
import ir.s_tag.musicplayer.Interfaces.ITimeSet;
import ir.s_tag.musicplayer.Interfaces.IVolSet;
import ir.s_tag.musicplayer.R;

import static ir.s_tag.musicplayer.MyApplication.TAG;


public class MusicPlayer extends View {

    AttributeSet attrs;
    private int width ,height ;
    Paint mShadow;
    private int volume = 0;
    private int time  = 0;
    private int volDeg , timeDeg ;

    Paint barPaint;
    Paint volPaint ;

    float x , y;
    Indicator timeIndicator , volIndicator;

    int diskX ;
    int diskY;
    int disk_radius;

    int targetTimeX ,targetTimeY;

    boolean userSelectedTime = false;
    boolean userSelectedVol = false;

    IVolSet mainVolSet;
    ITimeSet mainTimeSet;

    int barDelta = 60;
    int barThickness = 10;
    int timeBarX1;
    int timeBarY1;
    int timeBarX2;
    int timeBarY2;
    int timeBarCoverX1;
    int timeBarCoverY1;
    int timeBarCoverX2;
    int timeBarCoverY2;
    int volBarX1;
    int volBarY1;
    int volBarX2;
    int volBarY2;
    int volBarCoverX1;
    int volBarCoverY1;
    int volBarCoverX2;
    int volBarCoverY2;
    Paint circlePaint;
    Paint textPaint;
    double bgEndDotX;
    double bgEndDotY;
    String MusicTime = "00:00";

    Point bgVolEndPoint ;
    Point bgTimerEndPoint ;

    public MusicPlayer(Context context) {
        super(context);
        this.attrs = null;
    }

    public MusicPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
    }

    public MusicPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
    }

    public MusicPlayer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.attrs = attrs;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        init(attrs);
    }

    private void init(AttributeSet set){
        mShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        volPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        timeIndicator = new Indicator();
        volIndicator = new Indicator();

        diskX = width / 2 - (int)(width / 2.5);
        diskY = height / 2;
        disk_radius = height / 4;
        timeIndicator.setLeft(diskX);
        timeIndicator.setTop(height / 2 + disk_radius   + barDelta - 10  );
        timeIndicator.setOffset(new Point((int)timeIndicator.getLeft() ,(int) timeIndicator.getTop()));
        timeIndicator.setRadius(10);

        timeBarX1 = (diskX - disk_radius ) - barDelta  ;
        timeBarY1 = (diskY - disk_radius ) - barDelta  ;
        timeBarX2 = (diskX + disk_radius ) + barDelta ;
        timeBarY2 = (diskY + disk_radius  ) + barDelta ;


        volIndicator.setLeft(diskX);
        volIndicator.setTop((height / 2 - width / 2)  - barDelta - 10  );
        volIndicator.setOffset(new Point((int)volIndicator.getLeft(), (int)volIndicator.getTop()));
        volIndicator.setRadius(10);

        timeBarCoverX1 = timeBarX1 + barThickness ;
        timeBarCoverY1 = timeBarY1 + barThickness ;
        timeBarCoverX2 = timeBarX2 - barThickness ;
        timeBarCoverY2 = timeBarY2 - barThickness ;
//
//        bgEndDotX = Math.cos(Math.toRadians(5));
//        bgEndDotY = Math.sin(Math.toRadians(5));
//        bgEndDotX *= (width / 2 ) + 40;
//        bgEndDotY *= (width / 2 ) + 40;

        bgTimerEndPoint = getFromDeg(10);
        bgVolEndPoint = getFromDeg(10);


        volBarX1 = (diskX - disk_radius ) - barDelta  ;
        volBarY1 = (diskY - disk_radius ) - barDelta  ;
        volBarX2 = (diskX + disk_radius ) + barDelta ;
        volBarY2 = (diskY + disk_radius  ) + barDelta ;

        volBarCoverX1 = volBarX1 + barThickness ;
        volBarCoverY1 = volBarY1 + barThickness ;
        volBarCoverX2 = volBarX2 - barThickness ;
        volBarCoverY2 = volBarY2 - barThickness ;

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        textPaint.setColor(0x636363);
        textPaint.setColor(getResources().getColor(R.color.ui_text_color));
        textPaint.setTextSize(60);
        redefine();
    }

    private void redefine() {
//        Log.i(TAG, "onDraw: Vol volume = " + volume);

        volDeg = volume * 80 / 100;
        timeDeg= time * 80 / 100;
//        Log.i(TAG, "onDraw: Vol Deg = " + volDeg);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.i(TAG, "onDraw: userSelectedTime = "+ userSelectedTime);

        if(userSelectedTime){
            timeDeg = (int) getAngle(timeIndicator.getOffset().x,timeIndicator.getOffset().y , false);

            if(timeDeg > 180){
                timeDeg = 10;
            }
            if(timeDeg > 90){
                timeDeg = 90;
            }

            if(timeDeg < 10){
                timeDeg = 10;
            }
            timeDeg = 90 - timeDeg;
            if(mainTimeSet != null){
                mainTimeSet.onTimeSet(timeDeg* 100 / 80);
            }
        }


        if(userSelectedVol){
            volDeg = (int) getAngle(volIndicator.getOffset().x,volIndicator.getOffset().y , true);
            if(volDeg > 180){
                volDeg = 10;
            }
            if(volDeg > 90){
                volDeg = 90;
            }

            if(volDeg < 10){
                volDeg = 10;
            }

            volDeg = 90 - volDeg;
            if(mainVolSet != null){
                mainVolSet.onVolSet(volDeg * 100 / 80);
            }
        }

        /*** timer Bar Start **/


        /** bar bg color */
        barPaint.setColor(getResources().getColor(R.color.bar_bg));
        /** draw bar bg*/
        canvas.drawArc( timeBarX1, timeBarY1, timeBarX2, timeBarY2, 10 , 80 ,true , barPaint); // timer bg bar
        /** draw start Point Circle*/
        canvas.drawCircle( diskX, height / 2 + disk_radius + barDelta - (barThickness /2)  , barThickness /2 , barPaint); // timer bg start circle
        /** draw end Point Circle*/
        canvas.drawCircle((float) bgTimerEndPoint.x + diskX, (float) bgTimerEndPoint.y + diskY, barThickness  /2 , barPaint);// timer bg end circle
        /** draw Bar Progress*/
        barPaint.setColor(getResources().getColor(R.color.time_bar_bg));
        canvas.drawArc( timeBarX1, timeBarY1, timeBarX2, timeBarY2, 80 - (timeDeg - 10) , timeDeg ,true , barPaint);// timer progress
        canvas.drawCircle( diskX, (height / 2 +disk_radius ) + barDelta - (barThickness /2)  , barThickness  / 2 , barPaint);
        /** draw White Cover*/
        barPaint.setColor(getResources().getColor(R.color.CoverColor));
        canvas.drawArc( timeBarCoverX1, timeBarCoverY1, timeBarCoverX2, timeBarCoverY2, 0 , 100,true , barPaint); // timer bar cover
        /** draw time Indicator*/
        Point timePoint = getFromDeg(90 - timeDeg);

        barPaint.setColor(getResources().getColor(R.color.time_bar_bg));
        timeIndicator.setLeft(diskX +  timePoint.x);
        timeIndicator.setTop(diskY + timePoint.y);
        timeIndicator.setOffset(new Point((int)timeIndicator.getLeft() ,(int) timeIndicator.getTop()));
        canvas.drawCircle( diskX +  timePoint.x, diskY + timePoint.y, timeIndicator.getRadius() , barPaint);
        /*** timer Bar End **/


        /*** vol Bar Start  **/

        /** bar bg color */
        volPaint.setColor(getResources().getColor(R.color.bar_bg));
        /**draw bar bg*/
        canvas.drawArc( volBarX1, volBarY1, volBarX2, volBarY2, 270 , 80,true , volPaint);
        /**draw start Point Circle*/
        canvas.drawCircle( diskX, (height / 2 - disk_radius) - barDelta + (barThickness /2) , barThickness  / 2 , volPaint);
        /**draw end Point Circle*/
        canvas.drawCircle( bgVolEndPoint.x + diskX , diskY - bgVolEndPoint.y, barThickness  / 2  , volPaint);
        /**draw Bar Progress*/
        volPaint.setColor(getResources().getColor(R.color.vol_bar_bg));
        canvas.drawArc( volBarX1, volBarY1, volBarX2, volBarY2, 270 , volDeg ,true , volPaint);
        canvas.drawCircle( diskX, (height / 2 - disk_radius ) - barDelta + (barThickness /2)  , barThickness  / 2 , volPaint);
        /**draw White Cover*/
        volPaint.setColor(getResources().getColor(R.color.CoverColor));
        canvas.drawArc( volBarCoverX1, volBarCoverY1, volBarCoverX2 , volBarCoverY2, 270 , 90,true , volPaint);
        /**draw Vol Indicator*/
        volPaint.setColor(getResources().getColor(R.color.vol_bar_bg));
        Point volPoint = getFromDeg(90 - volDeg);
        volIndicator.setLeft(diskX +  volPoint.x);
        volIndicator.setTop(diskY - volPoint.y);
        volIndicator.setOffset(new Point((int)volIndicator.getLeft() ,(int) volIndicator.getTop()));

        canvas.drawCircle( diskX +  volPoint.x, diskY - volPoint.y, volIndicator.getRadius() , volPaint);
        /*** vol Bar End  **/


        /*** disk Start **/

        circlePaint.setColor(getResources().getColor(R.color.disk_color));

        circlePaint.setShadowLayer(50,0,0,getResources().getColor(R.color.disk_color));
        setLayerType(LAYER_TYPE_SOFTWARE, mShadow);


        canvas.drawCircle(diskX , diskY , disk_radius , circlePaint ); // Big Disk
        circlePaint.setShadowLayer(50,0,0,getResources().getColor(R.color.disk_color_dark));
        circlePaint.setColor(getResources().getColor(R.color.disk_center_border_color));
        canvas.drawCircle(width / 2 - (int)(width / 2.5) , height / 2 , disk_radius / 4 , circlePaint); // Big Center Disk
        circlePaint.setShadowLayer(20,0,0,getResources().getColor(R.color.disk_center_border_color));
        circlePaint.setColor(getResources().getColor(R.color.disk_center_color));
        canvas.drawCircle(width / 2 - (int)(width / 2.5) , height / 2 , disk_radius / 5 , circlePaint); // Big Border Disk

        /*** disk End **/

        /** texts! */
        textPaint.setTextSize(80);
        canvas.drawText("-", diskX - 10, (diskY - disk_radius) - barDelta - 10, textPaint);
        textPaint.setTextSize(50);
        canvas.drawText("+", bgVolEndPoint.x + diskX + 20 , diskY - bgVolEndPoint.y, textPaint);
        textPaint.setTextSize(30);
        canvas.drawText(MusicTime, diskX, height / 2 + disk_radius  + barDelta + barDelta - 20 , textPaint);


    }
    public float getAngle(float x , float y , boolean reverse) {
        float angle = (float) Math.toDegrees(Math.atan2(reverse ? diskY - y : y - diskY, x - diskX));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    private Point getFromDeg(int deg){
        double inRadian = Math.toRadians(deg);
        double volDotY = Math.sin(inRadian);
//        Log.i(TAG, "onDraw: "+timeDotY);
        double volDotX = Math.cos(inRadian);
        volDotY *= (disk_radius ) + barDelta - (barThickness /2);
        volDotX *= (disk_radius ) + barDelta - (barThickness /2);
        return new Point((int) volDotX ,(int) volDotY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN :
                if(x >= timeIndicator.getTouchX1() && x <= timeIndicator.getTouchX2()){
                    if(y >= timeIndicator.getTouchY1() && y <= timeIndicator.getTouchY2()){
//                        Log.i(TAG, "onDraw: touching!");
                        timeIndicator.setOffset(new Point((int)x, (int)y));
                        userSelectedTime = true;
                        AnimateIndicator(timeIndicator.getRadius() , 15 , true);
                        postInvalidate();
                    }
                }
                if(x >= volIndicator.getTouchX1() && x <= volIndicator.getTouchX2()){
                    if(y >= volIndicator.getTouchY1() && y <= volIndicator.getTouchY2()){
//                        Log.i(TAG, "onDraw: touching!");
                        volIndicator.setOffset(new Point((int)x, (int)y));
                        userSelectedVol = true;
                        AnimateIndicator(timeIndicator.getRadius() , 15 , false);
                        postInvalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(userSelectedTime){
                    timeIndicator.setOffset(new Point((int)x, (int)y));
                    postInvalidate();
                }
                if(userSelectedVol){
                    volIndicator.setOffset(new Point((int)x, (int)y));
//                    timeIndicator.setTop(y);
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                timeIndicator.setOffset(new Point((int)timeIndicator.getLeft(), (int)timeIndicator.getTop()));
                volIndicator.setOffset(new Point((int)volIndicator.getLeft(), (int)volIndicator.getTop()));
                if(userSelectedTime ){
                    AnimateIndicator(timeIndicator.getRadius() , 10 , true);
                    userSelectedTime = false;
                }
                if(userSelectedVol ){
                    userSelectedVol = false;
                    AnimateIndicator(timeIndicator.getRadius() , 10 , false);
                }
                break;
        }


        return true;
    }

    public void setEventListener(IVolSet volSet , ITimeSet timeSet){
        mainVolSet = volSet;
        mainTimeSet = timeSet;
    }

    public void setVolume(int vol){
        volume = vol;
//        Log.i(TAG, "setVolume: Hi!"+volume);
//        redefine();
        volDeg = volume * 80 / 100;
        postInvalidate();
    }

    public void setTime(int t , String dur){
        MusicTime = dur;
        time = t;
//        redefine();
        timeDeg= time* 80 / 100;
        postInvalidate();
    }

    private void AnimateIndicator(float from , float to , final boolean isTimer){
        ValueAnimator va = ValueAnimator.ofFloat(from , to);
//        va.setInterpolator(new OvershootInterpolator());
        va.setDuration(400);
        va.setRepeatCount(0);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(isTimer){
                    timeIndicator.setRadius((Float) valueAnimator.getAnimatedValue());
                }else{
                    volIndicator.setRadius((Float) valueAnimator.getAnimatedValue());
                }
                postInvalidate();
            }
        });
        va.start();
    }
    public void drawCurvedArrow(Canvas canvas ,int x1, int y1, int x2, int y2, int curveRadius, int lineWidth , Paint paint) {

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);

        final Path path = new Path();
        int midX            = x1 + ((x2 - x1) / 2);
        int midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
        float pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));

        path.moveTo(x1, y1);
        path.cubicTo(x1,y1,pointX, pointY, x2, y2);
        canvas.drawPath(path, paint);

    }
}
