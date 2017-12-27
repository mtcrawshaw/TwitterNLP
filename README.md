# TwitterNLP
# Program to use sentiment analysis on tweets to match users with other like minded users.
# Program takes screen name of a Twitter user, analyzes sentiments for tweets containing 100 or so specified "opinion words" (such as democrat, republican, country music, christianity, all their stems, and more), then performs the same analysis on all users who tweeted recently. 
# The program then outputs the screen names of the 5 users whose sentiments on tweets containing the "opinion words" most closely matched those of the initial user. 
# 
# Unfortunately, Twitter limits the rate at which developers can sample tweets as they are happening, and any time the program tries to run, we get throttled by 
Twitter and we have to wait for a specified window, which grows with every throttling, making the program basically unusable. That's okay though, I basically just 
made the program to learn the Stanford CoreNLP toolkit and the Twitter API. It was fun and I learned something, so even with the throttling I'm satisfied. 
