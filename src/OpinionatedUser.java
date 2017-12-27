import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class OpinionatedUser {
	//Private data
	private String screenName;
	private double[] opinions;
	
	private int NUM_OPINION_WORDS;
	
	// Constructors
	public OpinionatedUser() {
		getOpinionWords();
		screenName = "";
		opinions = new double[NUM_OPINION_WORDS];
	}
	public OpinionatedUser(String name, double[] op) {
		getOpinionWords();
		screenName = name;
		opinions = op;
	}
	public OpinionatedUser(OpinionatedUser U) {
		getOpinionWords();
		screenName = U.getScreenName();
		opinions = Arrays.copyOf(U.getOpinions(), NUM_OPINION_WORDS);
	}
	
	// Mutators
	public void setScreenName(String name) {
		screenName = name;
	}
	public void setOpinions(double[] o) {
		assert o.length == NUM_OPINION_WORDS;
		opinions = o;
	}
	public void setOpinion(int i, double o) {
		opinions[i] = o;
	}
	
	// Accessors
	public String getScreenName() {
		return screenName;
	}
	public double[] getOpinions() {
		return opinions;
	}
	public double getOpinion(int i) {
		return opinions[i];
	}
	
	// Methods
	public void calculateOpinions() {
		ArrayList<String> opinionWords = getOpinionWords();
		ArrayList<Status> tweets = this.getTweets();
		
		for (int i = 0; i < NUM_OPINION_WORDS; i++) {
			int numTweetsWithWord = 0;
			int sumSentiments = 0;
			
			for (Status tweet : tweets) {
				String text = tweet.getText().trim().toLowerCase();
				ArrayList<String> words = new ArrayList<String>(Arrays.asList(text.split(" ")));
				
				if (words.contains(opinionWords.get(i))) {
					int sentiment = SentimentAnalysis.getSentiment(tweet.getText());
					if (sentiment != -1) {
						sumSentiments += sentiment;
						numTweetsWithWord++;
					}
				}
			}
			
			if (numTweetsWithWord != 0) {
				opinions[i] = (double)sumSentiments / (double)numTweetsWithWord;
			} else {
				opinions[i] = 2;
			}
		}
	}
	private ArrayList<String> getOpinionWords() {
		Scanner reader = new Scanner("opinionWords.txt");
		ArrayList<String> opinionWords = new ArrayList<String>();
		
		while (reader.hasNext()) {
			opinionWords.add(reader.nextLine());
		}
		
		NUM_OPINION_WORDS = opinionWords.size();
		reader.close();
		return opinionWords;
	}
	public ArrayList<Status> getTweets() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		
		ArrayList<Status> statuses = new ArrayList<Status>();
		int pageNumber = 1;
		
		while (true) {
			try {
				int size = statuses.size();
				Paging page = new Paging(pageNumber++, 100);
				statuses.addAll(twitter.getUserTimeline(screenName, page));
				if (statuses.size() == size) break;
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		}
		
		return statuses;
	}
	public double distanceTo(OpinionatedUser U) {
		double dist = 0;
		double[] UOpinions = U.getOpinions();
		
		for (int i = 0; i < NUM_OPINION_WORDS; i++) {
			dist += (opinions[i] - UOpinions[i]) * (opinions[i] - UOpinions[i]);
		}
		
		return Math.sqrt(dist);
	}
}
