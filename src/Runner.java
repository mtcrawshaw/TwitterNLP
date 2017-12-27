import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class Runner {
	public static void main(String[] args) {
		final int NUM_RECOMMENDATIONS = 5;
		String screenName = args[0];
		
		OpinionatedUser user = new OpinionatedUser();
		user.setScreenName(screenName);
		user.calculateOpinions();
		HashSet<User> recentUsers = getRecentUsers(1000);
		
		ArrayList<User> closestUsers = new ArrayList<User>();
		ArrayList<Double> distances = new ArrayList<Double>();
		
		for (User u : recentUsers) {
			OpinionatedUser recentUser = new OpinionatedUser();
			recentUser.setScreenName(u.getScreenName());
			recentUser.calculateOpinions();
			double distance = user.distanceTo(recentUser);
			
			if (closestUsers.size() < NUM_RECOMMENDATIONS) {
				closestUsers.add(u);
				distances.add(distance);
			} else {
				int i = 0;
				
				while (i < NUM_RECOMMENDATIONS && distances.get(i) < distance) {
					closestUsers.add(i, u);
					distances.add(i, distance);
				}
			}
		}
		
		for (int i = 0; i < closestUsers.size(); i++) {
			System.out.println(closestUsers.get(i).toString());
			System.out.println(distances.get(i) + "\n");
		}
	}
	
	// Returns a list of a random sampling of numTweets of the most recent tweets
	private static ArrayList<Status> getRecentTweets(int numTweets){
		ArrayList<Status> recentTweets = new ArrayList<Status>();
		
		StatusListener listener = new StatusListener(){
	        public void onStatus(Status status) {
	            recentTweets.add(status);
	        }
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	        }
			@Override
			public void onScrubGeo(long arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}
	    };
	    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	    twitterStream.addListener(listener);
	    twitterStream.sample("en");
	    
	    while (recentTweets.size() < numTweets) {
	    	try {
				TimeUnit.MILLISECONDS.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	    twitterStream.removeListener(listener);
	    
	    while (recentTweets.size() > numTweets) recentTweets.remove(recentTweets.size() - 1);
		return recentTweets;
	}
	
	// Returns a list of (approximately) numUsers users that have tweeted recently
	private static HashSet<User> getRecentUsers(int numUsers) {
		HashSet<User> users = new HashSet<User>();
		
		ArrayList<Status> recentTweets = getRecentTweets(numUsers);
		for (Status tweet : recentTweets) {
			User currentUser = tweet.getUser();
			if (!users.contains(currentUser)) users.add(currentUser);
		}
		
		return users;
	}
}
