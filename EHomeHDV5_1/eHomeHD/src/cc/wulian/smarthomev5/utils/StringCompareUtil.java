package cc.wulian.smarthomev5.utils;

import java.util.Comparator;
import java.util.TreeSet;

public class StringCompareUtil
{
	public static class ResultInfo
	{
		public Object obj;
		public float similar;
	}

	private static final Comparator<ResultInfo> SIMILAR_ORDER = new Comparator<ResultInfo>()
	{
		@Override
		public int compare( ResultInfo lhs, ResultInfo rhs ){
			Float similar1 = lhs.similar;
			Float similar2 = rhs.similar;
			return similar1.intValue() - similar2.intValue();
		}
	};

	private final TreeSet<ResultInfo> mConfidenceInfo = new TreeSet<ResultInfo>(SIMILAR_ORDER);

	public void startCompare( String src, String target, Object obj ){
		float similar = StringMatchUtil.Compare.getSimilarityRatio(src, target);
		if (isSimilarity(similar)){
			ResultInfo resultInfo = new ResultInfo();
			resultInfo.obj = obj;
			resultInfo.similar = similar;
			mConfidenceInfo.add(resultInfo);
		}
	}

	public ResultInfo getBestSimilarResultInfo(){
		ResultInfo resultInfo = null;
		if (!mConfidenceInfo.isEmpty()){
			resultInfo = mConfidenceInfo.last();
		}
		return resultInfo;
	}

	private boolean isSimilarity( float similar ){
		return 0.15F <= similar && similar <= 1.0F;
	}
}
