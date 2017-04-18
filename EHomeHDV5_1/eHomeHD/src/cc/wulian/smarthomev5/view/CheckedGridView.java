package cc.wulian.smarthomev5.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CheckedGridView extends GridView
{
	/**
	 * Layout as a result of using the navigation keys
	 */
	static final int LAYOUT_MOVE_SELECTION = 6;

	/**
	 * Normal list that does not indicate choices
	 */
	public static final int CHOICE_MODE_NONE = 0;

	/**
	 * The list allows up to one choice
	 */
	public static final int CHOICE_MODE_SINGLE = 1;

	/**
	 * The list allows multiple choices
	 */
	public static final int CHOICE_MODE_MULTIPLE = 2;

	/**
	 * The list allows multiple choices in a modal selection mode
	 */
	public static final int CHOICE_MODE_MULTIPLE_MODAL = 3;

	/**
	 * Controls if/how the user may choose/check items in the list
	 */
	int mChoiceMode = CHOICE_MODE_NONE;
	
	private boolean mInLayout = false;

	private SparseBooleanArray mCheckStates;

	/**
	 * Running state of which IDs are currently checked. If there is a value for a given key, the checked state for that ID is true and the value holds the last
	 * known position in the adapter for that id.
	 */
	private SparseIntArray mCheckedIdStates;

	private ListAdapter mAdapter;

	private int mCheckedItemCount;

	public CheckedGridView( Context context )
	{
		super(context);
	}

	public CheckedGridView( Context context, AttributeSet attrs )
	{
		super(context, attrs);
	}

	public CheckedGridView( Context context, AttributeSet attrs, int defStyle )
	{
		super(context, attrs, defStyle);
	}

	@Override
	public void setAdapter( ListAdapter adapter ){
		super.setAdapter(adapter);
		mAdapter = adapter;
		if (mAdapter != null){
			if (mChoiceMode != CHOICE_MODE_NONE && mAdapter.hasStableIds() && mCheckedIdStates == null){
				mCheckedIdStates = new SparseIntArray();
			}
		}

		if (mCheckStates != null){
			mCheckStates.clear();
		}

		if (mCheckedIdStates != null){
			mCheckedIdStates.clear();
		}
	}
	
	@Override
	protected void onLayout( boolean changed, int l, int t, int r, int b ){
		mInLayout = true;
		super.onLayout(changed, l, t, r, b);
		mInLayout = false;
	}

  @Override
  public void requestLayout() {
      if (!mInLayout) {
          super.requestLayout();
      }
  }

	/**
	 * Returns the number of items currently selected. This will only be valid if the choice mode is not {@link #CHOICE_MODE_NONE} (default).
	 * 
	 * <p>
	 * To determine the specific items that are currently selected, use one of the <code>getChecked*</code> methods.
	 * 
	 * @return The number of items currently selected
	 * 
	 * @see #getCheckedItemPosition()
	 * @see #getCheckedItemPositions()
	 * @see #getCheckedItemIds()
	 */
	public int getCheckedItemCount(){
		return mCheckedItemCount;
	}

	public boolean isItemChecked( int position ){
		if (mChoiceMode != CHOICE_MODE_NONE && mCheckStates != null){
			return mCheckStates.get(position);
		}
		else{
			return false;
		}
	}

	public int getCheckedItemPosition(){
		if (mChoiceMode == CHOICE_MODE_SINGLE && mCheckStates != null && mCheckStates.size() == 1){
			return mCheckStates.keyAt(0);
		}
		else{
			return INVALID_POSITION;
		}
	}

	public SparseBooleanArray getCheckedItemPositions(){
		if (mChoiceMode != CHOICE_MODE_NONE){ 
			return mCheckStates; 
		}
		return null;
	}

  /**
   * Returns the set of checked items ids. The result is only valid if the
   * choice mode has not been set to {@link #CHOICE_MODE_NONE} and the adapter
   * has stable IDs. ({@link ListAdapter#hasStableIds()} == {@code true})
   *
   * @return A new array which contains the id of each checked item in the
   *         list.
   */
	public long[] getCheckedItemIds() {
      if (mChoiceMode == CHOICE_MODE_NONE || mCheckedIdStates == null || mAdapter == null) {
          return new long[0];
      }

      final SparseIntArray idStates = mCheckedIdStates;
      final int count = idStates.size();
      final long[] ids = new long[count];

      for (int i = 0; i < count; i++) {
          ids[i] = idStates.keyAt(i);
      }

      return ids;
  }

	/**
	 * Clear any choices previously set
	 */
	public void clearChoices(){
		if (mCheckStates != null){
			mCheckStates.clear();
		}
		if (mCheckedIdStates != null){
			mCheckedIdStates.clear();
		}
		mCheckedItemCount = 0;
		updateOnScreenCheckedViews();
	}

	public void setItemChecked( int position, boolean value ){
		if (mChoiceMode == CHOICE_MODE_NONE){ 
			return; 
		}

		if (mChoiceMode == CHOICE_MODE_MULTIPLE){
			boolean oldValue = mCheckStates.get(position);
			mCheckStates.put(position, value);
			if (mCheckedIdStates != null && mAdapter.hasStableIds()){
				if (value){
					mCheckedIdStates.put((int) mAdapter.getItemId(position), position);
				}
				else{
					mCheckedIdStates.delete((int) mAdapter.getItemId(position));
				}
			}
			if (oldValue != value){
				if (value){
					mCheckedItemCount++;
				}
				else{
					mCheckedItemCount--;
				}
			}
		}
		else{
			boolean updateIds = mCheckedIdStates != null && mAdapter.hasStableIds();
			// Clear all values if we're checking something, or unchecking the currently
			// selected item
			if (value || isItemChecked(position)){
				mCheckStates.clear();
				if (updateIds){
					mCheckedIdStates.clear();
				}
			}
			// this may end up selecting the value we just cleared but this way
			// we ensure length of mCheckStates is 1, a fact getCheckedItemPosition relies on
			if (value){
				mCheckStates.put(position, true);
				if (updateIds){
					mCheckedIdStates.put((int) mAdapter.getItemId(position), position);
				}
			}
		}

    // Do not generate a data change while we are in the layout phase
    if (!mInLayout){
        requestLayout();
    }
	}

	@Override
	public boolean performItemClick( View view, int position, long id ){
		boolean handled = false;
		boolean dispatchItemClick = true;

		if (mChoiceMode != CHOICE_MODE_NONE){
			handled = true;
			boolean checkedStateChanged = false;

			if (mChoiceMode == CHOICE_MODE_SINGLE){
				boolean newValue = !mCheckStates.get(position, false);
				if (newValue){
					mCheckStates.clear();
					mCheckStates.put(position, true);
					if (mCheckedIdStates != null && mAdapter.hasStableIds()){
						mCheckedIdStates.clear();
						mCheckedIdStates.put((int) mAdapter.getItemId(position), position);
					}
					mCheckedItemCount = 1;
				}
				else if (mCheckStates.size() == 0 || !mCheckStates.valueAt(0)){
					mCheckedItemCount = 0;
				}
				checkedStateChanged = true;
			}

			if (checkedStateChanged){
				updateOnScreenCheckedViews();
			}
		}

		if (dispatchItemClick){
			handled |= super.performItemClick(view, position, id);
		}

		return handled;
	}

	/**
   * Perform a quick, in-place update of the checked or activated state
   * on all visible item views. This should only be called when a valid
   * choice mode is active.
   */
  private void updateOnScreenCheckedViews() {
      final int firstPos = getFirstVisiblePosition();
      final int count = getChildCount();
      for (int i = 0; i < count; i++) {
          final View child = getChildAt(i);
          final int position = firstPos + i;

          if (child instanceof Checkable) {
              ((Checkable) child).setChecked(mCheckStates.get(position));
			}
      }
  }

	public void setChoiceMode( int choiceMode ){
		mChoiceMode = choiceMode;
		if (mChoiceMode != CHOICE_MODE_NONE){
			if (mCheckStates == null){
				mCheckStates = new SparseBooleanArray();
			}
			if (mCheckedIdStates == null && mAdapter != null && mAdapter.hasStableIds()){
				mCheckedIdStates = new SparseIntArray();
			}
			// Modal multi-choice mode only has choices when the mode is active. Clear them.
			if (mChoiceMode == CHOICE_MODE_MULTIPLE_MODAL){
				clearChoices();
				setLongClickable(true);
			}
		}
	}

	@Override
	public Parcelable onSaveInstanceState(){
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		if (mCheckStates != null){
			final SparseBooleanArray array = new SparseBooleanArray();
			final int count = mCheckStates.size();

			for (int i = 0; i < count; i++){
				array.put(mCheckStates.keyAt(i), mCheckStates.valueAt(i));
			}
			ss.checkState = array;
		}
		if (mCheckedIdStates != null){
			final SparseIntArray idState = new SparseIntArray();
			final int count = mCheckedIdStates.size();
			for (int i = 0; i < count; i++){
				idState.put(mCheckedIdStates.keyAt(i), mCheckedIdStates.valueAt(i));
			}
			ss.checkIdState = idState;
		}
		ss.checkedItemCount = mCheckedItemCount;
		return ss;
	}

	@Override
	public void onRestoreInstanceState( Parcelable state ){
		SavedState ss = (SavedState) state;

		super.onRestoreInstanceState(ss.getSuperState());

		if (ss.checkState != null){
			mCheckStates = ss.checkState;
		}

		if (ss.checkIdState != null){
			mCheckedIdStates = ss.checkIdState;
		}

		mCheckedItemCount = ss.checkedItemCount;
	}

	static class SavedState extends BaseSavedState
	{
		int checkedItemCount;
		SparseBooleanArray checkState;
		SparseIntArray checkIdState;

		/**
		 * Constructor called from {@link ListView#onSaveInstanceState()}
		 */
		SavedState( Parcelable superState )
		{
			super(superState);
		}

		/**
		 * Constructor called from {@link #CREATOR}
		 */
		private SavedState( Parcel in )
		{
			super(in);
			checkedItemCount = in.readInt();
			checkState = in.readSparseBooleanArray();
			final int N = in.readInt();
			if (N > 0){
				checkIdState = new SparseIntArray();
				for (int i = 0; i < N; i++){
					final int key = (int) in.readLong();
					final int value = in.readInt();
					checkIdState.put(key, value);
				}
			}
		}

		@Override
		public void writeToParcel( Parcel out, int flags ){
			super.writeToParcel(out, flags);
			out.writeInt(checkedItemCount);
			out.writeSparseBooleanArray(checkState);
			final int N = checkIdState != null ? checkIdState.size() : 0;
			out.writeInt(N);
			for (int i = 0; i < N; i++){
				out.writeLong(checkIdState.keyAt(i));
				out.writeInt(checkIdState.valueAt(i));
			}
		}

		@Override
		public String toString(){
			return "GridView.SavedState{" + Integer.toHexString(System.identityHashCode(this))
					+ " checkState=" + checkState + "}";
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
		{
			@Override
			public SavedState createFromParcel( Parcel in ){
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray( int size ){
				return new SavedState[size];
			}
		};
	}
}
