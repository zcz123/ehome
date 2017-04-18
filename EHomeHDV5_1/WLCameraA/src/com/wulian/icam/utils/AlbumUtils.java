/**
 * Project Name:  iCam
 * File Name:     AlbulmUtils.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2015年4月1日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.wulian.icam.R.string;
import com.wulian.icam.model.AlbumEntity;

/**
 * @ClassName: AlbulmUtils
 * @Function: 图库操作工具类
 * @Date: 2015年4月1日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class AlbumUtils {
	// 对袁建胜思路的点评:从数据库中查询是用作全局的图片浏览器，而我们的相册是针对特定目录的，从数据库查询无疑思路就有问题，费时费力，很愚蠢！
	private ContentResolver mContentResolver = null;
	private Uri mImageUri = null;

	public AlbumUtils(Context context) {
		this.mContentResolver = context.getContentResolver();
		this.mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}

	/**
	 * @MethodName: getAllPicDir
	 * @Function: 用于找带有关键字的所有文件夹（包含图片）
	 * @author: yuanjs
	 * @date: 2015年4月1日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param 存取图片的路径关键字
	 * @return
	 */
	public List<AlbumEntity> getAllPicDir(String keyWord) {
		/**
		 * 临时的辅助类，用于防止同一个文件夹的多次扫描
		 */
		HashSet<String> mDirPaths = new HashSet<String>();
		List<AlbumEntity> mAlbumEntities = new ArrayList<AlbumEntity>();
		Cursor mCursor = null;
		try {
			mCursor = mContentResolver.query(mImageUri, null,
					MediaStore.Images.Media.DATA + " like ?",
					new String[] { "%" + keyWord + "%" },
					MediaStore.Images.Media.DATE_MODIFIED);
			while (mCursor != null && mCursor.moveToNext()) {
				// 获取图片的路径
				String path = mCursor.getString(mCursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				Utils.sysoInfo("path======> " + path);
				String width = mCursor.getString(mCursor
						.getColumnIndex(MediaStore.Images.Media.WIDTH));
				// 过滤损坏了的图片
				// if(width==null){
				// continue;
				// }
				// 获取该图片的父路径名
				File parentFile = new File(path).getParentFile();
				if (parentFile == null)
					continue;
				long timeMis = parentFile.lastModified();
				String time = new SimpleDateFormat("yyyy年MM月dd日:HH:mm:ss")
						.format(new Date(timeMis));
				String dirPath = parentFile.getAbsolutePath();
				AlbumEntity albumEntity = null;
				// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
				if (mDirPaths.contains(dirPath)) {
					continue;
				} else {
					mDirPaths.add(dirPath);
					// 初始化imageFloder
					albumEntity = new AlbumEntity();
					albumEntity.setPath(dirPath);
					albumEntity.setFirstImagePath(path);
					albumEntity.setTime(time);
				}

				int picSize = parentFile.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						if (filename.endsWith(".jpg")
								|| filename.endsWith(".png")
								|| filename.endsWith(".jpeg"))
							return true;
						return false;
					}
				}).length;
				albumEntity.setCount(picSize);
				mAlbumEntities.add(albumEntity);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mCursor != null) {
				mCursor.close();
				mCursor = null;
				mDirPaths = null;
			}
		}
		return mAlbumEntities;
	}

	/**
	 * @MethodName: allPicDirInaFile
	 * @Function: 得到指定文件夹目录下所有图片路径
	 * @author: yuanjs
	 * @date: 2015年4月1日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param filepath
	 *            指定文件夹路径
	 * @return
	 */
	public List<String> getAllPicDirInaFile(String filepath) {
		List<String> allDirList = new ArrayList<String>();
		Cursor cursor = null;
		try {
			cursor = mContentResolver.query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
					MediaStore.Images.Media.DATA + " like ?",
					new String[] { filepath + "%" },
					MediaStore.Images.Media.DATE_MODIFIED);
			while (cursor != null && cursor.moveToNext()) {
				String dir = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
				String width = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.WIDTH));
				// if(width!=null&&Integer.parseInt(width)>0){
				// allDirList.add(dir);
				// }
				allDirList.add(dir);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return allDirList;
	}

	/**
	 * @MethodName: deletePicByDir
	 * @Function: 删除指定路径的图片
	 * @author: yuanjs
	 * @date: 2015年4月1日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param dir
	 *            该图片的路径
	 */
	public void deletePicByDir(String dir) {
		mContentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				MediaStore.Images.Media.DATA + "=?", new String[] { dir });
	}

	/**
	 * @Function 获取相册目录下的所有相册，基于文件系统
	 * @author Wangjj
	 * @date 2015年6月9日
	 * @param 相册目录
	 * @return
	 */

	public List<AlbumEntity> getAlbums(String filepath) {
		List<AlbumEntity> albumList = new ArrayList<AlbumEntity>();
		File albumDir = new File(filepath);
		if (!albumDir.exists()) {
			albumDir.mkdirs();
		}
		File[] albumDirItems = albumDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		// 对相册目录排序
		Arrays.sort(albumDirItems, new Comparator<File>() {

			@Override
			public int compare(File lhs, File rhs) {
				if (lhs.lastModified() > rhs.lastModified())
					return -1;// 较新的靠前排
				else if (lhs.lastModified() < rhs.lastModified()) {
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < albumDirItems.length; i++) {
			File albumDirItem = albumDirItems[i];
			File[] jpgImgs = albumDirItem.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					return filename.toLowerCase(Locale.ENGLISH)
							.endsWith(".jpg");
				}
			});
			if (jpgImgs.length > 0) {
				AlbumEntity entity = new AlbumEntity();
				entity.setPath(albumDirItem.getAbsolutePath());
				String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
						Locale.ENGLISH).format(new Date(albumDirItem
						.lastModified()));// 文件夹的修改时间为放入最后一张图片的时间
				entity.setTime(time);
				entity.setFirstImagePath(jpgImgs[jpgImgs.length - 1]
						.getAbsolutePath());// 默认时间升序排序，所以最后一张图片为最新拍摄
				entity.setCount(jpgImgs.length);
				albumList.add(entity);
			}
		}
		return albumList;
	}

	public List<String> getAllPicDirFromAFile(String filepath) {
		List<String> picList = new ArrayList<String>();
		List<File> fileList = new ArrayList<File>();
		File file = new File(filepath);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				int size = files.length;
				if (size > 0) {
					fileList = Arrays.asList(files);
					sortList(fileList, false);
					for (int i = 0; i < size; i++) {
						picList.add(fileList.get(i).getAbsolutePath());
					}
				}
			}
		}

		return picList;
	}
	/**
	 * @MethodName:  getAlbumEntityFromAFile
	 * @Function:    依据filepath得到AlbumEntity
	 * @author:      yuanjs
	 * @date:        2015年11月18日
	 * @email:       jiansheng.yuan@wuliangroup.com
	 * @param filepath 某个摄像头图片所在文件夹路径
	 * @return {@link AlbumEntity}对象
	 */
	public static AlbumEntity getAlbumEntityFromAFile(String filepath) {
		AlbumEntity entity = null;
		List<String> picList = new ArrayList<String>();
		List<File> fileList = new ArrayList<File>();
		File file = new File(filepath);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				int size = files.length;
				if (size > 0) {
					fileList = Arrays.asList(files);
					sortList(fileList, true);
					for (int i = 0; i < size; i++) {
						picList.add(fileList.get(i).getAbsolutePath());
					}
				}
			}
		}
		if(picList.size()>0){
			entity = new AlbumEntity();
			entity.setPath(filepath);
			String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.ENGLISH).format(new Date(file
					.lastModified()));
			entity.setTime(time);
			entity.setFirstImagePath(picList.get(picList.size()-1));// 默认时间升序排序，所以最后一张图片为最新拍摄
			entity.setCount(picList.size());
		}
		return entity;
	}

	public List<String> loadJpgs(String filePath) {
		File[] jpgImgs = new File(filePath).listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return filename.toLowerCase(Locale.ENGLISH).endsWith(".jpg");
			}
		});

		// 对图片排序
		Arrays.sort(jpgImgs, new Comparator<File>() {

			@Override
			public int compare(File lhs, File rhs) {
				if (lhs.lastModified() > rhs.lastModified())
					return -1;// 较新的靠前排
				else if (lhs.lastModified() < rhs.lastModified()) {
					return 1;
				}
				return 0;
			}
		});
		List<String> paths = new ArrayList<String>();
		for (int i = 0; i < jpgImgs.length; i++) {
			paths.add(jpgImgs[i].getAbsolutePath());
		}
		return paths;
	}

	/**
	 * @MethodName: sortList
	 * @Function: 根据修改时间为文件列表排序
	 * @author: yuanjs
	 * @date: 2015年4月21日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param list
	 *            排序的文件列表
	 * @param asc
	 *            否升序排序 true为升序 false为降序
	 */
	public static void sortList(List<File> list, final boolean asc) {
		// 按修改日期排序
		Collections.sort(list, new Comparator<File>() {
			public int compare(File file, File newFile) {
				if (file.lastModified() > newFile.lastModified()) {
					if (asc) {
						return 1;
					} else {
						return -1;
					}
				} else if (file.lastModified() == newFile.lastModified()) {
					return 0;
				} else {
					if (asc) {
						return -1;
					} else {
						return 1;
					}
				}

			}
		});
	}

	/**
	 * @MethodName: deletePicByPath
	 * @Function: 删除
	 * @author: yuanjs
	 * @date: 2015年4月16日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param path
	 */
	public void deletePicByPath(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}
}
