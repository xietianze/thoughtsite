Thoughtsite is a discussions/forum app designed for Google App Engine. The main features of the app are:
<br><br>
- a flexible system that could be used for any kind of a discussion forum.<br>
- voting, tagging, comments and a reputation point system for users.<br>
- full text search on App Engine with <a href='http://lucene.apache.org/'>Apache Lucene</a>.<br>
- search for threads by tags  or by keywords. Threads can also be linked to from user profiles.<br>
- users gain reputation points based on community votes for their contributions.<br>
- full fledged user profiles with info, points, contributions, user's personal tag cloud, etc.<br>
- basic duplication detection filters to detect similar threads so posters can avoid creating a new thread if one already exists.<br>
- basic spam and gaming filters (self-voting, cross-voting, etc.)<br>
- full-fledged admin section that allows moderation of individual posts and users. Users can flag objectionable content or trolls.<br>
<br>
UPDATE!<br>
A lot of you have reported problems getting the code to deploy - we have now launched an official deployment of the Thoughtsite code at <a href='http://thoughtsitedemos.appspot.com/'>http://thoughtsitedemos.appspot.com/</a>

The deployment is a little light on data, but hopefully should be intuitive to navigate and fun to play around with. The project uses OpenId but doesn't yet have a good UI; so if you wish to use Google as the OpenId provider, you will need to put this in the "Sign in" dialog: "<a href='https://www.google.com/accounts/o8/id'>https://www.google.com/accounts/o8/id</a>". We know this looks very ugly, but that's all we have until we can fix it.<br>
