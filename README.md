#MemeGenerator
====================
###What is this madness?
What good is a new device that can't make memes? Worry not, glassholes. Your wish is my command. 

Install this app and ask your glass to make a meme by saying - "OK Glass, make a meme" and let the magic happen.

###Teach me how to debuggie
Here's what happens:

* Manifest:

	* We request permission for external storage, camera and internet
	* We list several Android entities: 
		* MemeService Service
		* MainPage Activity
		* AddCaption Activity
		* LiveCardActivity Activity
		
* MemeService: : This service catches the voice trigger, creates a live card and a broadcast receiver that will allow us to modify the live card. Then triggers MainPage.
* MainPage: This activity triggers the image capture intent and waits for Android to send back an image. On a successful image capture, we launch AddCaption.
* AddCaption: This activity has menu options to trigger caption input. Upon return from entering voice input, we change menu option to Share. Selecting share closes activities and sends user to Live card. Uploading image triggers the broadcast we initialized earlier with an updated message = link to Imgur
