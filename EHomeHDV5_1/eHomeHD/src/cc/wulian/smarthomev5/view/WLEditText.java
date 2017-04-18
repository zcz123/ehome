package cc.wulian.smarthomev5.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import cc.wulian.smarthomev5.R;

/**
 * 自定义的输入框
 * 
 * @author xiaozhi
 * @创作日期 2014-7-24
 */
public class WLEditText extends EditText {
    private boolean isDelImgVis = false;
    private Drawable delDrawableImg = null;
    private Drawable leftDrawableImg = null;
    private DelTextWatcher watcher = null;
    private boolean delFromEnd = false; 

  
	public boolean isDelFromEnd() {
		return delFromEnd;
	}

	public void setDelFromEnd(boolean delFromEnd) {
		this.delFromEnd = delFromEnd;
	}

	/**
     * 输入监听回调函数
     */
    private WLInputTextWatcher wlInputTextWatcher;

    public WLEditText(Context context) {
        this(context, null);
    }

    public WLEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WLEditText);
        this.delFromEnd = a.getBoolean(R.styleable.WLEditText_del_from_end, false);
        init(context);
    }

    @SuppressLint("NewApi")
	private void init(Context c) {
    	this.setFocusable(true);
    	this.setEnabled(true);
    	this.setClickable(true);
    	this.setCursorVisible(true);
    	this.setFocusableInTouchMode(true);
        leftDrawableImg = this.getCompoundDrawables()[0];
        delDrawableImg = c.getResources().getDrawable(R.drawable.icon_delete);
        if (watcher == null) {
            watcher = new DelTextWatcher();
            this.addTextChangedListener(watcher);
        }
    }

    public WLEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WLEditText, defStyle, 0);
        this.delFromEnd = a.getBoolean(R.styleable.WLEditText_del_from_end, false);
        this.init(context);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
            Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            if (!TextUtils.isEmpty(this.getText()))
                setDelDrawable(true);
        } else {
            setDelDrawable(false);
        }
    }
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new ZanyInputConnection(super.onCreateInputConnection(outAttrs),true);
    }
    private class ZanyInputConnection extends InputConnectionWrapper {
        public ZanyInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }
        @Override
        public boolean sendKeyEvent(KeyEvent event) {
        	//add_by_yanzy_at_2016-5-27:删除应该保留Android习惯
            if (WLEditText.this.delFromEnd && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
            	WLEditText edit = WLEditText.this;
            	String text = edit.getText().toString();
            	if(text.length() > 0){
            		String newText = text.substring(0,text.length() - 1);
            		edit.setText(newText);
            		Selection.setSelection(edit.getText(), newText.length());
            	}
                return false;
            }
            return super.sendKeyEvent(event);
        }
    }
    private class DelTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            if (s.length() > 0) {
                setDelDrawable(true);
            }
            if (wlInputTextWatcher != null) {
                wlInputTextWatcher.beforeTextChanged(s, start, count, after);
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            if (s.length() > 0) {
                setDelDrawable(true);
            }

            if (wlInputTextWatcher != null) {
                wlInputTextWatcher.onTextChanged(s, start, before, count);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                setDelDrawable(true);
            } else {
                setDelDrawable(false);
            }

            if (wlInputTextWatcher != null) {
                wlInputTextWatcher.afterTextChanged(s);
            }
        }
    }

    /**
     * 
     * @param i
     *            :show drawable true / false
     */
    private void setDelDrawable(boolean i) {

        if (i) {
            if (!isDelImgVis) {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawableImg, null,
                        delDrawableImg, null);
                isDelImgVis = true;
            }
        } else {
            if (isDelImgVis) {
                setCompoundDrawablesWithIntrinsicBounds(leftDrawableImg, null,
                        null, null);
                isDelImgVis = false;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDelImgVis && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_DOWN)) {
            boolean isClean =(event.getX() > (getWidth() - getTotalPaddingRight()))&&
                    (event.getX() < (getWidth() - getPaddingRight()));
            if (isClean) {
		       setText("");
		       setDelDrawable(false);
            }
        }
        return super.onTouchEvent(event);
    }

    public void registWLIputTextWatcher(WLInputTextWatcher inputTextWatcher) {
        this.wlInputTextWatcher = inputTextWatcher;
    }

    public static interface WLInputTextWatcher {
        void beforeTextChanged(CharSequence s, int start, int count, int after);

        void onTextChanged(CharSequence s, int start, int before, int count);

        void afterTextChanged(Editable s);
    }
}
