
# Bot Checker
Questioning on why the chatters list, seems to always have some wierd usernames or the same people as in other streams?  

Well, those users could be bots, and what are they doing in your channel? Who knows, maybe trying steal sensitive data from your chatters, act as part of a centralized attack, the sky is the limit for them...

That's the reason that drove me to develop this.




## How does it work?

The bot works in two ways:  
- Manual: When the broadcaster or a mod writes `!bots` in the chat.
- Automatic: By checking the `JOIN` notification sent by twitch when a user joins the chat.

 1. Retrieves the users in chat or uses the one that twitch notified.
 2. Retrieves [online bots data](https://api.twitchinsights.net/v1/bots/online)
 3. Checks if the user is present in the known bots list.
 4. Checks if the user must be ignored according to the `CHATTERS_TO_IGNORE` env variable.
 5. If the user is a bot and should not be ignored, we proceeed to call Twitch's API to ban them.

## What is required?
1. An account to act as a bot (**make sure that it can perform moderation actions**), you could use the same one for broadcasts.
2. A client ID, for this follow [the official guide.](https://dev.twitch.tv/docs/authentication/register-app/)
3. Obtain the token (it's recommended to do it in a private window, in case twitch requires your authorization). For this process, we are going to follow the [implicit OAuth token flow](https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/#implicit-grant-flow)  
Scopes that we are going to use, you can check them out [here (Twitch API)](https://dev.twitch.tv/docs/api/reference/) and [here (basic scopes for chat)](https://dev.twitch.tv/docs/authentication/scopes/#chat-and-pubsub-scopes)

```
channel:manage:polls+channel:read:polls+
user:read:email+chat:read+chat:edit+
channel:moderate+moderator:read:chatters+
moderator:manage:shoutouts+moderator:manage:banned_users
```

Example on how the URL will look like with the scopes, remember to **replace YOUR_CLIENT_ID tag with the client ID from the console.**
```
https://id.twitch.tv/oauth2/authorize?response_type=token&
client_id=YOUR_CLIENT_ID
&redirect_uri=http://localhost:3000&scope=channel%3Amanage%3Apolls+channel%3Aread%3Apolls
+user%3Aread%3Aemail+chat%3Aread+chat%3Aedit+channel%3Amoderate+whispers%3Aedit
+moderator%3Aread%3Achatters+moderator%3Amanage%3Ashoutouts
+moderator%3Amanage%3Abanned_users&state=c3ab8aa609ea11e793ae92361f002671
```
4. Copy the **access_token** value that has been obtained from the URL:
```
http://localhost:3000/
    #access_token=zxzxzxzxzxzxzxzxzxz
    &scope=channel%3Amanage%3Apolls+channel%3Aread%3Apolls+
    user%3Aread%3Aemail+chat%3Aread+chat%3Aedit+channel%3Amoderate+
    whispers%3Aedit+moderator%3Aread%3Achatters+ 
    moderator%3Amanage%3Ashoutouts+moderator%3Amanage%3Abanned_users
    &state=c3ab8aa609ea11e793ae92361f002671
    &token_type=bearer
```

#### IMPORTANT
Take in consideration that this method only gives you a token for **90 days**, after that, you need to renew it manually and update it on the environment variable.
## Environment Variables

To run this project, you will need to add the following environment variables to your docker/compose file

`BOT_USER`=<YOUR_BOT_NAME>

`BOT_TOKEN`=<YOUR_BOT_TOKEN_WITH_SCOPES>

`CHANNEL_TO_JOIN`=<CHANNEL_THAT_BOT_WILL_VERIFY>

`BOT_CLIENT_ID`=<THE_BOT_CLIENT_FROM_DEVELOPER_CONSOLE>

`BOT_CHECKER_ACTIVE`=Y

`CHATTERS_TO_IGNORE`=<USERS_THAT_WILL_BE_IGNORED_SEPARATED_BY_COMMA>


## How to run it?

Clone the project

```bash
  git clone https://github.com/ckmu32/Bot-Checker
```

Go to the project directory

```bash
  cd Bot-Checker
```

Modify the compose file to add the environment variables data.

Execute the compose file

```bash
  docker compose up --detach
```


## License

[MIT](https://choosealicense.com/licenses/mit/)

