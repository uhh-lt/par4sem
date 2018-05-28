package mturk.util;

import java.io.IOException;
import java.util.Map;

import com.googlecode.jweb1t.JWeb1TSearcher;

import de.tudarmstadt.lt.lm.service.StringProviderMXBean;

public class NgramUtils {

	public static long[] get2gramFreqs(String target, String candidate, String bf, String af, JWeb1TSearcher web1t)
			throws IOException {
		long targetFrq = 0l;
		long candFrq = 0l;
		// bigrams
		if (bf.trim().split(" ").length > 0) {
			String[] bf1s = bf.trim().split(" ");
			targetFrq = targetFrq + web1t.getFrequency(bf1s[bf1s.length - 1] + " " + target);
			candFrq = candFrq + web1t.getFrequency(bf1s[bf1s.length - 1] + " " + candidate);
		}

		if (af.trim().split(" ").length > 0) {
			String[] af1s = af.trim().split(" ");
			targetFrq = targetFrq + web1t.getFrequency(target + " " + af1s[0]);
			candFrq = candFrq + web1t.getFrequency(candidate + " " + af1s[0]);
		}

		return new long[] { targetFrq, candFrq };
	}

	public static long[] get3gramFreqs(String target, String candidate, String bf, String af, JWeb1TSearcher web1t)
			throws IOException {
		long targetFrq = 0l;
		long candFrq = 0l;

		// trigrams
		if (bf.trim().split(" ").length > 0 && af.trim().split(" ").length > 0) {
			String[] bf1s = bf.trim().split(" ");
			String[] af1s = af.trim().split(" ");
			targetFrq = targetFrq + web1t.getFrequency(bf1s[bf1s.length - 1] + " " + target + " " + af1s[0]);
			candFrq = candFrq + web1t.getFrequency(bf1s[bf1s.length - 1] + " " + candidate + " " + af1s[0]);
		}

		return new long[] { targetFrq, candFrq };
	}

	public static long[] get5gramFreqs(String target, String candidate, String bf, String af, JWeb1TSearcher web1t)
			throws IOException {
		long targetFrq = 0l;
		long candFrq = 0l;

		// five grams
		if (bf.trim().split(" ").length > 1 && af.trim().split(" ").length > 1) {
			String[] bf1s = bf.trim().split(" ");
			String[] af1s = af.trim().split(" ");
			targetFrq = targetFrq + web1t.getFrequency(
					bf1s[bf1s.length - 2] + " " + bf1s[bf1s.length - 1] + " " + target + " " + af1s[0] + " " + af1s[1]);
			candFrq = candFrq + web1t.getFrequency(
					bf1s[bf1s.length - 2] + " " + bf1s[bf1s.length - 1] + " " + candidate + af1s[0] + " " + af1s[1]);
		}
		return new long[] { targetFrq, candFrq };
	}

	public static long get2gramFreqs(String target, String bf, String af, JWeb1TSearcher web1t) throws IOException {
		long targetFrq = 0l;
		// bigrams
		if (bf.trim().split(" ").length > 0) {
			String[] bf1s = bf.trim().split(" ");
			String gram = bf1s[bf1s.length - 1] + " " + target;
				targetFrq = targetFrq + web1t.getFrequency(gram);
		}

		if (af.trim().split(" ").length > 0) {
			String[] af1s = af.trim().split(" ");
			String gram = target + " " + af1s[0];

				targetFrq = targetFrq + web1t.getFrequency(gram);
		}
		return targetFrq;
	}

	public static long getunigramFreqs(String target, JWeb1TSearcher web1t) throws IOException {

		return web1t.getFrequency(target);

	}

	public static long get3gramFreqs(String target, String bf, String af, JWeb1TSearcher web1t) throws IOException {
		long targetFrq = 0l;

		if (bf.trim().split(" ").length > 0 && af.trim().split(" ").length > 0) {
			String[] bf1s = bf.trim().split(" ");
			String[] af1s = af.trim().split(" ");
			String gram = bf1s[bf1s.length - 1] + " " + target + " " + af1s[0];

			return targetFrq + web1t.getFrequency(gram);

		}
		return targetFrq;
	}

	public static double getPerplexity(String target, String bf, String af, StringProviderMXBean strprovider)
			throws Exception {
		double perplexity = 0;

		/*
		 * // trigrams if (bf.trim().split(" ").length > 0 &&
		 * af.trim().split(" ").length > 0) { String[] bf1s =
		 * bf.trim().split(" "); String[] af1s = af.trim().split(" "); String
		 * gram = bf1s[bf1s.length - 1] + " " + target + " " + af1s[0]; return
		 * strprovider.getPerplexity(target, false) /
		 * (Math.abs(strprovider.getPerplexity(gram, false)) + 1); } if
		 * (Double.isNaN(perplexity)) { perplexity = 0; }
		 */
		return perplexity;
	}

	public static double getseqProb(String target, String bf, String af, StringProviderMXBean strprovider)
			throws Exception {
		double seqProb = 0;
		// trigrams
		/*
		 * if (bf.trim().split(" ").length > 0 && af.trim().split(" ").length >
		 * 0) { String[] bf1s = bf.trim().split(" "); String[] af1s =
		 * af.trim().split(" "); String gram = bf1s[bf1s.length - 1] + " " +
		 * target + " " + af1s[0]; return
		 * strprovider.getSequenceLog10Probability(target) /
		 * (Math.abs(strprovider.getSequenceLog10Probability(gram)) + 1); } if
		 * (Double.isNaN(seqProb)) { seqProb = 0; }
		 */
		return seqProb;
	}

	public static long get5gramFreqs(String target, String bf, String af, JWeb1TSearcher web1t) throws IOException {
		long targetFrq = 0l;

		// five grams
		if (bf.trim().split(" ").length > 1 && af.trim().split(" ").length > 1) {
			String[] bf1s = bf.trim().split(" ");
			String[] af1s = af.trim().split(" ");
			String gram = bf1s[bf1s.length - 2] + " " + bf1s[bf1s.length - 1] + " " + target + " " + af1s[0] + " "
					+ af1s[1];
			return targetFrq + web1t.getFrequency(gram);

		}
		return targetFrq;
	}
}
