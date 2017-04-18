/******************************************************************************
 *                                                                            *
 * Copyright (c) 2011 by TUTK Co.LTD. All Rights Reserved.                    *
 *                                                                            *
 *                                                                            *
 * Class: Fifo.java                                                           *
 *                                                                            *
 * Author: joshua ju                                                          *
 *                                                                            *
 * Date: 2011-05-14                                                           *
 *                                                                            *
 ******************************************************************************/

package com.wulian.iot.bean.tutk;
import java.util.LinkedList;
import com.tutk.IOTC.AVFrame;
public class AVFrameQueueRecord {
	private volatile LinkedList<AVFrame> listDataRecord = new LinkedList<AVFrame>();
	private volatile int mSizeRecord = 0;
	private volatile int mKeepFramSize;

	public synchronized int getCount() {
		return mSizeRecord;
	}

	public synchronized void addLast(AVFrame node) {

		if (mSizeRecord > 1500) {

			boolean bFirst = true;

			while (true) {
				if(listDataRecord.isEmpty())	
					break ;
				AVFrame frame = listDataRecord.get(0);

				if (bFirst) {

					if (frame.isIFrame())
						System.out.println("drop I frame");
					else
						System.out.println("drop p frame");

					listDataRecord.removeFirst();
					mSizeRecord--;
					System.out.println("----------3");

				} else {

					if (frame.isIFrame())
						break;
					else {
						System.out.println("drop p frame");
						listDataRecord.removeFirst();
						mSizeRecord--;
						System.out.println("----------4");
					}
				}

				bFirst = false;
			}
		}

		listDataRecord.addLast(node);
		mSizeRecord++;
		
	}

	public synchronized AVFrame removeHead() {

		if (mSizeRecord == 0)
			return null;
		else {
			AVFrame frame = listDataRecord.removeFirst();
			mSizeRecord--;
			
			return frame;
		}
	}
	
    public synchronized void setKeepFram(int keepFrame) {
        mKeepFramSize = keepFrame;
    }

	public synchronized void removeAll() {

		if (!listDataRecord.isEmpty())
			listDataRecord.clear();

		mSizeRecord = 0;
		System.out.println("----------removeAll--AVFrameQueueRecord");
	}
	
	public synchronized boolean isFirstIFrame() {
		return listDataRecord != null && !listDataRecord.isEmpty() && listDataRecord.get(0).isIFrame();
	}
}
