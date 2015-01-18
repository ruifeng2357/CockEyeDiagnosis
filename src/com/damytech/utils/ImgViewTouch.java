package com.damytech.utils;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import com.damytech.patient.R;

public class ImgViewTouch extends ImageView
{
    Matrix matrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    PointF last = new PointF();
    PointF start = new PointF();
    float minScale = 1f;
    float maxScale = 3f;
    float[] m;

    float redundantXSpace, redundantYSpace;

    float width, height;
    static final int CLICK = 3;
    float saveScale = 1f;
    float right, bottom, origWidth, origHeight, bmWidth, bmHeight;

    ScaleGestureDetector mScaleDetector;
    Context context;

	ArrayList<Area> mAreaList=new ArrayList<Area>();

	ArrayList<OnImageMapClickedHandler> mCallbackList;

	boolean bShowArea = true;
	
	/////////////////////////////////////////////////////////
	// set value for first scale
	private float fScale = 0.0f;
	private float fStartX = 0.0f;
	private float fStartY = 0.0f;
	private boolean bScaled = false;
	
    public ImgViewTouch(Context context)
    {
        super(context);
        sharedConstructing(context);
    }

    public ImgViewTouch(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        sharedConstructing(context);
    }

    private void sharedConstructing(Context context)
    {
        super.setClickable(true);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        matrix.setTranslate(1f, 1f);
        m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                mScaleDetector.onTouchEvent(event);

                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
                PointF curr = new PointF(event.getX(), event.getY());

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        last.set(event.getX(), event.getY());
                        start.set(last);
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG)
                        {
                            float deltaX = curr.x - last.x;
                            float deltaY = curr.y - last.y;
                            float scaleWidth = Math.round(origWidth * saveScale);
                            float scaleHeight = Math.round(origHeight * saveScale);
                            if (scaleWidth < width)
                            {
                                deltaX = 0;
                                if (y + deltaY > 0)
                                    deltaY = -y;
                                else if (y + deltaY < -bottom)
                                    deltaY = -(y + bottom);
                            }
                            else if (scaleHeight < height)
                            {
                                deltaY = 0;
                                if (x + deltaX > 0)
                                    deltaX = -x;
                                else if (x + deltaX < -right)
                                    deltaX = -(x + right);
                            }
                            else
                            {
                                if (x + deltaX > 0)
                                    deltaX = -x;
                                else if (x + deltaX < -right)
                                    deltaX = -(x + right);

                                if (y + deltaY > 0)
                                    deltaY = -y;
                                else if (y + deltaY < -bottom)
                                    deltaY = -(y + bottom);
                            }
                            matrix.postTranslate(deltaX, deltaY);
                            last.set(curr.x, curr.y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        mode = NONE;
                        int xDiff = (int) Math.abs(curr.x - start.x);
                        int yDiff = (int) Math.abs(curr.y - start.y);
                        if (xDiff < CLICK && yDiff < CLICK)
                            performClick();
                        
                        if (isScaleOk())
                        	onScreenTapped((int)event.getX(), (int)event.getY());
                        
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                }
                setImageMatrix(matrix);
                invalidate();
                return true; // indicate event was handled
            }
        });
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        if (bm != null)
        {
            bmWidth = bm.getWidth();
            bmHeight = bm.getHeight();
        }
    }

    public void setMaxZoom(float x)
    {
        maxScale = x;
    }

    private class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector)
        {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            float mScaleFactor = (float) Math.min(
                    Math.max(.95f, detector.getScaleFactor()), 1.05);
            float origScale = saveScale;
            saveScale *= mScaleFactor;
            if (saveScale > maxScale)
            {
                saveScale = maxScale;
                mScaleFactor = maxScale / origScale;
            }
            else if (saveScale < minScale)
            {
                saveScale = minScale;
                mScaleFactor = minScale / origScale;
            }
            right = width * saveScale - width - (2 * redundantXSpace * saveScale);
            bottom = height * saveScale - height
                    - (2 * redundantYSpace * saveScale);
            if (origWidth * saveScale <= width || origHeight * saveScale <= height)
            {
                matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2);
                if (mScaleFactor < 1)
                {
                    matrix.getValues(m);
                    float x = m[Matrix.MTRANS_X];
                    float y = m[Matrix.MTRANS_Y];
                    if (mScaleFactor < 1)
                    {
                        if (Math.round(origWidth * saveScale) < width)
                        {
                            if (y < -bottom)
                                matrix.postTranslate(0, -(y + bottom));
                            else if (y > 0)
                                matrix.postTranslate(0, -y);
                        }
                        else
                        {
                            if (x < -right)
                                matrix.postTranslate(-(x + right), 0);
                            else if (x > 0)
                                matrix.postTranslate(-x, 0);
                        }
                    }
                }
            }
            else
            {
                matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(),
                        detector.getFocusY());
                matrix.getValues(m);
                float x = m[Matrix.MTRANS_X];
                float y = m[Matrix.MTRANS_Y];
                if (mScaleFactor < 1)
                {
                    if (x < -right)
                        matrix.postTranslate(-(x + right), 0);
                    else if (x > 0)
                        matrix.postTranslate(-x, 0);
                    if (y < -bottom)
                        matrix.postTranslate(0, -(y + bottom));
                    else if (y > 0)
                        matrix.postTranslate(0, -y);
                }
            }
            return true;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        // Fit to screen.
        float scale;
        float scaleX = (float) width / (float) bmWidth;
        float scaleY = (float) height / (float) bmHeight;
        scale = Math.min(scaleX, scaleY);
        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
        saveScale = 1f;

        // Center the image
        redundantYSpace = (float) height - (scale * (float) bmHeight);
        redundantXSpace = (float) width - (scale * (float) bmWidth);
        redundantYSpace /= (float) 2;
        redundantXSpace /= (float) 2;

        matrix.postTranslate(redundantXSpace, redundantYSpace);

        origWidth = width - 2 * redundantXSpace;
        origHeight = height - 2 * redundantYSpace;
        right = width * saveScale - width - (2 * redundantXSpace * saveScale);
        bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
        setImageMatrix(matrix);
        
        ///////// change area position
        if (scale != Double.POSITIVE_INFINITY)
        {
        	if (!bScaled)
        	{
        		fStartX = redundantXSpace;
            	fStartY = redundantYSpace;
            	fScale = scale;
            	bScaled = true;
            	
            	updatePos();
        	}
        }        
    }
    
    
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////
    
    private void updatePos()
    {
    	float x, y;
    	
    	for (Area a : mAreaList)
    	{
    		x = a.getOriginX() * fScale + fStartX;
    		y = a.getOriginY() * fScale + fStartY;
    		a.setOriginX(x);
    		a.setOriginY(y);
    	}
    }
    
    /**
	 * Create a new area and add to tracking
	 * @param name
	 * @param coords
	 * @param id
	 * @return
	 */
	public Area addShape(String name, String coords, int id) {	
		Area a = null;
		int _id=0;
		
		try {
			_id = id;
		}
		catch (Exception e) {
		   _id = 0;
		}
		if (_id != 0) {
			String[] v = coords.split(",");
			if (v.length == 3) {
				a = new CircleArea(_id,name, Float.parseFloat(v[0]),
						Float.parseFloat(v[1]),
						Float.parseFloat(v[2])
						);
			}
			if (a != null) {
				addArea(a);
			}
		}
		return a;
	}
	
	public void addArea( Area a ) {
		mAreaList.add(a);
	}	
	
	private boolean isScaleOk()
	{
		boolean bRet = false;
		
		if (saveScale == minScale)
			bRet = true;
		
		return bRet;
	}
	
	/**
	 * Paint the view
	 *   image first, location decorations next, bubbles on top
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (isScaleOk())
		{
			drawLocations(canvas);
		}
	}
	
	protected void drawLocations(Canvas canvas) {
		for (Area a : mAreaList) {
			a.onDraw(canvas);
		}	
	}
	
	/*
	 * on clicked handler add/remove support
	 */
	public void addOnImageMapClickedHandler( OnImageMapClickedHandler h ) {
		if (h != null) {
			if (mCallbackList == null) {
				mCallbackList = new ArrayList<OnImageMapClickedHandler>();
			}
			mCallbackList.add(h);
		}
	}
	
	/*
	 * Screen tapped x, y is screen coord from upper left and does not account
	 * for scroll
	 */
	void onScreenTapped(int x, int y) {
		boolean missed = true;
		// adjust for scroll
		int testx = x;
		int testy = y;
		
		// then check for area taps
		for (Area a : mAreaList) {
			if (a.isInArea((float)testx,(float)testy)) {
				if (mCallbackList != null) {
					for (OnImageMapClickedHandler h : mCallbackList) {
						h.onImageMapClicked(a.getId());						
					}
				}
				missed=false;
				// only fire clicked for one area
				break;
			}
		}

		if (missed) {
			// managed to miss everything, clear bubbles
			//mBubbleMap.clear();
			invalidate();
		}
	}
	
	
	/*
	 * Begin map area support
	 */
	/**
	 *  Area is abstract Base for tappable map areas
	 *   descendants provide hit test and focal point
	 */
    abstract class Area {
		int _id;
		String _name;
		HashMap<String,String> _values;
		Bitmap _decoration=null;
		
    	public Area(int id, String name) {
    		_id = id;
			if (name != null) {
				_name = name;
			}
    	}
    	
		public int getId() {
			return _id;
		}			
		
		public String getName() {
			return _name;
		}
		
		// all xml values for the area are passed to the object
		// the default impl just puts them into a hashmap for
		// retrieval later
		public void addValue(String key, String value) {
			if (_values == null) {
				_values = new HashMap<String,String>();
			}
			_values.put(key, value);
		}
				
		public String getValue(String key) {
			String value=null;
			if (_values!=null) {
				value=_values.get(key);
			}
			return value;
		}
		
		// a method for setting a simple decorator for the area
		public void setBitmap(Bitmap b) {
			_decoration = b;
		}
		
		// an onDraw is set up to provide an extensible way to
		// decorate an area.  When drawing remember to take the
		// scaling and translation into account
		public void onDraw(Canvas canvas) {
			_decoration = BitmapFactory.decodeResource(getResources(), R.drawable.detail_mark);
			if (_decoration != null) {
				float x = getOriginX() - 5;
				float y = getOriginY() - 5;
				canvas.drawBitmap(_decoration, x, y, null);
			}
		}
		
		abstract boolean isInArea(float x, float y);
		abstract float getOriginX();
		abstract float getOriginY();
		abstract void setOriginX(float x);
		abstract void setOriginY(float y);
	}
	
	/**
	 * Circle Area
	 */
	class CircleArea extends Area {		
		float _x;
		float _y;
		float _radius;		
						
		CircleArea(int id, String name, float x, float y, float radius) {
			super(id,name);
//			_x = x * fScale + fStartX;
//			_y = y * fScale + fStartY;
			_x = x;
			_y = y;
			_radius = radius;
			
		}		
		
		public boolean isInArea(float x, float y) {
			boolean ret = false;
			
			float dx = _x-x;
			float dy = _y-y;
			
			// if tap is less than radius distance from the center
			float d = (float)Math.sqrt((dx*dx)+(dy*dy));
			if (d<_radius) {
				ret = true;
			}

			return ret;
		}
		
		public float getOriginX() {
			return _x;
		}
		
		public float getOriginY() {
			return _y;
		}
		
		public void setOriginX(float x) {
			_x = x;
		}
		
		public void setOriginY(float y) {
			_y = y;
		}
	}
	
	/**
	 * Map tapped callback interface
	 */
	public interface OnImageMapClickedHandler {
		/**
		 * Area with 'id' has been tapped
		 * @param id
		 */
		void onImageMapClicked(int id);
	}
}