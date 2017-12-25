package tamil.developers.thirukkural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.PopupWindow;

class DragDropManager
{
    private static DragDropManager instance;
    private Activity mActivity;
    private List<View> dropZones;
    private Map<View, Integer> dropZoneStates;
    private Map<View, DropZoneListener> dropZoneListeners;
    private PopupWindow popoup;
    private MotionEvent firstEvent;
    private Rect rect;
    private Object item;

    static DragDropManager getInstance()
    {
        if (instance == null) instance = new DragDropManager();
        return instance;
    }

    private DragDropManager()
    {
    }

    void init(Activity a)
    {
        mActivity = a;
        dropZones = new ArrayList<>();
        dropZoneListeners = new HashMap<>();
        dropZoneStates = new HashMap<>();
        rect = new Rect();
    }

    void addDropZone(View zone, DropZoneListener zonelistener)
    {
        dropZones.add(zone);
        dropZoneListeners.put(zone, zonelistener);
        dropZoneStates.put(zone, 0);
    }

    private void checkDropZones(MotionEvent event)
    {
        boolean isOver;
        HashSet<DropZoneListener> listeners = new HashSet<>(dropZoneListeners.values());

        for (View zone : dropZones)
        {
            int[] location = new int[2];
            zone.getLocationInWindow(location);
            zone.getDrawingRect(rect);
            rect.offset(location[0], location[1]);
            isOver = rect.contains((int) event.getRawX(), (int) event.getRawY());

            switch (dropZoneStates.get(zone))
            {
                case 0:
                    if (isOver)
                    {
                        for(DropZoneListener listener:listeners)
                        {
                            listener.OnDragZoneEntered(zone, item); 
                        }
                        dropZoneStates.put(zone, 1);
                    }

                    break;
                case 1:
                    if (!isOver)
                    {
                        for(DropZoneListener listener:listeners)
                        {
                            listener.OnDragZoneLeft(zone, item);    
                        }
                        dropZoneStates.put(zone, 0);
                    }
                    else if (isOver && event.getAction()==MotionEvent.ACTION_UP)
                    {
                        for(DropZoneListener listener:listeners)
                        {
                            listener.OnDropped(zone, item); 
                        }
                        dropZoneStates.put(zone, 0);
                    }
                    break;
            }
        }
    }

    void startDragging(final View dragView, Object item)
    {
        this.item = item;
        // Copy view Bitmap (Clone Object visual)
        ImageView view = new ImageView(mActivity);
        view.measure(dragView.getWidth(), dragView.getHeight());

        Bitmap returnedBitmap = Bitmap.createBitmap(dragView.getWidth(), dragView.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        dragView.draw(canvas);

        view.setBackgroundDrawable(new BitmapDrawable(dragView.getResources(), returnedBitmap));

        // Set up Window
        popoup = new PopupWindow(view, dragView.getWidth(), dragView.getHeight());
        popoup.setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        // set window at position
        int[] location = new int[2];
        dragView.getLocationInWindow(location);
        popoup.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.NO_GRAVITY, location[0], location[1]);
        // Switch call Backs
        callbackDefault = mActivity.getWindow().getCallback();
        mActivity.getWindow().setCallback(callback);
    }

    private android.view.Window.Callback callbackDefault;

    private android.view.Window.Callback  callback = new android.view.Window.Callback()
    {
        @Override
        public boolean onSearchRequested(SearchEvent event)
        {
            return false;
        }

        @Override
        public boolean dispatchGenericMotionEvent(MotionEvent event)
        {
            return false;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event)
        {
            return false;
        }

        @Override
        public boolean dispatchKeyShortcutEvent(KeyEvent event)
        {
            return false;
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event)
        {
            return false;
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event)
        {
            checkDropZones(event);

            //if (event.getAction() == MotionEvent.ACTION_DOWN)
            //{
                // popoup.update((int)event.getRawX(), (int)event.getRawY(), -1,
                // -1);
            //}

            if (event.getAction() == MotionEvent.ACTION_MOVE)
            {
                if (firstEvent == null) firstEvent = MotionEvent.obtain(event);

                // Log.v("EVENT","X:"+event.getRawX() + " _X:" + location[0] +
                // " __X:" + firstEvent.getRawX());
                // Log.v("EVENT","Y:"+event.getRawY() + " _Y:" + location[1] +
                // " __Y:" + firstEvent.getRawY());

                float pos_x = event.getRawX() + (-popoup.getWidth() / 2);
                float pos_y = event.getRawY() + (-popoup.getHeight() / 2);

                popoup.update((int) pos_x, (int) pos_y, -1, -1);

            }

            if (event.getAction() == MotionEvent.ACTION_UP)
            {
                popoup.dismiss();
                mActivity.getWindow().setCallback(callbackDefault);
            }

            return false;
        }

        @Override
        public boolean dispatchTrackballEvent(MotionEvent event)
        {
            return false;
        }

        @Override
        public void onActionModeFinished(ActionMode mode)
        {
        }

        @Override
        public void onActionModeStarted(ActionMode mode)
        {
        }

        @Override
        public void onAttachedToWindow()
        {
        }

        @Override
        public void onContentChanged()
        {
        }

        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu)
        {
            return false;
        }

        @Override
        public View onCreatePanelView(int featureId)
        {
            return null;
        }

        @Override
        public void onDetachedFromWindow()
        {
        }

        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item)
        {
            return false;
        }

        @Override
        public boolean onMenuOpened(int featureId, Menu menu)
        {
            return false;
        }

        @Override
        public void onPanelClosed(int featureId, Menu menu)
        {
        }

        @Override
        public boolean onPreparePanel(int featureId, View view, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onSearchRequested()
        {
            return false;
        }

        @Override
        public void onWindowAttributesChanged(android.view.WindowManager.LayoutParams attrs)
        {
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus)
        {
        }

        @Override
        public ActionMode onWindowStartingActionMode(Callback callback)
        {
            return null;
        }

        @Override
        public ActionMode onWindowStartingActionMode(Callback callback, int i)
        {
            return null;
        }
    };

    public interface DropZoneListener
    {

        void OnDragZoneEntered(View zone, Object item);

        void OnDragZoneLeft(View zone, Object item);

        void OnDropped(View zone, Object item);

    }
}
