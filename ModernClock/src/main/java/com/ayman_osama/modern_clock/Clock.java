package com.ayman_osama.modern_clock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Clock extends View{


        private static final double SIDE = 1000;

        private Paint paint1, paint2, paint3, paint4, paint5, paint6;
        private CartesianPoint c_origin, c_hours, c_minutes;
        private PolarPoint p_hours, p_minutes, p_seconds;
        private float ball_hour_radius, ball_minutes_radius;
        private boolean change, nightMode= false, hideSeconds = false ;
        private int side, handlesColor = Color.GRAY, shadow = Color.BLACK;

        private Timer timer;
        private final TimerTask task = new TimerTask() {
            @Override
            public void run() {

                Calendar calendar = Calendar.getInstance();

                int time_seconds = calendar.get(Calendar.SECOND);
                double angle_seconds = 6*time_seconds;

                int time_minutes = calendar.get(Calendar.MINUTE);
                double angle_minutes = 6*time_minutes;

                int time_hours = calendar.get(Calendar.HOUR);
                double angle_hours = 30*time_hours+0.5*time_minutes;


                p_seconds = new PolarPoint(SIDE /2- SIDE /18,angle_seconds);

                p_minutes = new PolarPoint(p_seconds.radius - SIDE /12,angle_minutes);
                p_minutes.rotate(-90);
                p_minutes.invert();
                c_minutes = new CartesianPoint(p_minutes);
                c_minutes.translate(c_origin);

                p_hours = new PolarPoint(p_minutes.radius - SIDE /7,angle_hours);
                p_hours.rotate(-90);
                p_hours.invert();
                c_hours = new CartesianPoint(p_hours);
                c_hours.translate(c_origin);

                if(p_seconds.angle == 0) change = !change;
                if(change) p_seconds.rotate(-360);

                ball_minutes_radius = (float) (SIDE /30);

                ball_hour_radius = (float) (SIDE /24);

                paint5.setShader(new LinearGradient(
                        c_hours.x-ball_hour_radius,
                        c_hours.y,
                        c_hours.x+ball_hour_radius,
                        c_hours.y+ball_hour_radius,
                        Color.argb(0,0,0,0),
                        shadow,
                        Shader.TileMode.MIRROR
                ));
                paint5.setAlpha(100);

                paint6.setShader(new LinearGradient(
                        c_minutes.x-ball_minutes_radius,
                        c_minutes.y,
                        c_minutes.x+ball_minutes_radius,
                        c_minutes.y+ball_minutes_radius,
                        Color.argb(0,0,0,0),
                        shadow,
                        Shader.TileMode.MIRROR
                ));
                paint6.setAlpha(100);

                invalidate();

            }
        };

        public Clock(Context context) {
            super(context);
        }

        public Clock(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();

            change = false;

            c_origin  = new CartesianPoint(SIDE / 2, SIDE / 2);
            if (nightMode) shadow = Color.WHITE;

            preparePaints();

            timer = new Timer();
            timer.scheduleAtFixedRate(task,0,1000);

        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            timer.cancel();
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            side = getMeasuredWidth();
            setMeasuredDimension(side, side);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.scale((float) (side/SIDE), (float) (side/SIDE));

            canvas.drawCircle(c_origin.x, c_origin.y,p_hours.radius,paint2);

            canvas.drawCircle(c_origin.x, c_origin.y,p_minutes.radius,paint3);

            canvas.drawCircle(c_hours.x, c_hours.y, ball_hour_radius,paint1);
            canvas.drawCircle(c_hours.x, c_hours.y, ball_hour_radius,paint5);

            canvas.drawCircle(c_minutes.x, c_minutes.y, ball_minutes_radius, paint1);
            canvas.drawCircle(c_minutes.x, c_minutes.y, ball_minutes_radius, paint6);

            if(!hideSeconds){
                canvas.drawArc(
                        (c_origin.x-p_seconds.radius),
                        (c_origin.y-p_seconds.radius),
                        (c_origin.x+p_seconds.radius),
                        (c_origin.y+p_seconds.radius),
                        -90F,
                        (float) Math.toDegrees(p_seconds.angle),
                        false,
                        paint4
                );}



        }


        private void preparePaints(){

            paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint1.setStyle(Paint.Style.FILL);
            paint1.setColor(handlesColor);

            paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setStrokeWidth((float) (SIDE /10));
            paint2.setColor(shadow);
            paint2.setAlpha(30);

            paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint3.setStrokeWidth((float) (SIDE /12));
            paint3.setColor(paint2.getColor());

            paint3.setAlpha(30);
            paint3.setStyle(Paint.Style.STROKE);

            paint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint4.setStyle(Paint.Style.STROKE);
            paint4.setStrokeWidth((float) (SIDE /46));
            paint4.setColor(paint1.getColor());

            paint5 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint5.setStyle(Paint.Style.FILL);

            paint6 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint6.setStyle(Paint.Style.FILL);


        }

        public void setHandlesColor(int color){
            this.handlesColor = color;
        }

        public int getHandlesColor() {
            return handlesColor;
        }

        public void setNightMode(boolean nightMode) {
            this.nightMode = nightMode;
        }

        public boolean isNightMode() {
            return nightMode;
        }

        public boolean isHideSeconds() {
            return hideSeconds;
        }

        public void setHideSeconds(boolean hideSeconds) {
            this.hideSeconds = hideSeconds;
        }

        private static class CartesianPoint {

            public float x;
            public float y;

            public CartesianPoint() {
            }

            public CartesianPoint(double x_integer, double y_integer) {
                this.x = (float) x_integer;
                this.y = (float) y_integer;
            }

            public CartesianPoint(@NonNull PolarPoint point){
                this.x = (float) (point.radius*Math.cos(point.angle));
                this.y = (float) (-point.radius*Math.sin(point.angle));
            }

            public void translate(@NonNull CartesianPoint point){
                this.x += point.x;
                this.y += point.y;
            }

            public void invert(){
                this.x = -x;
                this.y = -y;
            }
        }

        private static class PolarPoint {

            public float radius;
            public float angle;

            public PolarPoint() {
            }

            public PolarPoint(double radius_integer, double angle_degree) {
                this.radius = (float) radius_integer;
                this.angle =  (float) Math.toRadians(angle_degree);
            }

            public PolarPoint(@NonNull CartesianPoint point){
                this.radius = (float) Math.sqrt(point.x*point.x+point.y*point.y);
                this.angle  = (float) Math.atan2(point.x,point.y);
            }

            public void rotate(double angle_degree){
                this.angle += Math.toRadians(angle_degree);
            }

            public void invert(){
                this.angle = -angle;
            }
        }


    }

