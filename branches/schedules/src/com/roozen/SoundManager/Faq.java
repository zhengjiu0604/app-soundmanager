/**
 * Copyright 2009 Daniel Roozen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed 
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */
package com.roozen.SoundManager;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Faq extends Activity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        
        WebView web = (WebView) findViewById(R.id.webview);
        web.loadData(
        		"<html><body><h1>How to Use</h1>\n\n" +
        		"<p>Slide your finger along any of the volume bars to change the volume.</p>\n\n" +
        		"<p>Use the Menu to switch screens to set vibration settings or toggle the ringmode.</p>\n\n" +
        		"<p>To set a timer:</p>\n\n" +
        		"<ol>\n" +
        		"<li>Press the Set Timer button next to any of the volume settings.</li>\n" +
        		"<li>Choose the time of day you first want the volume to change.</li>\n" +
        		"<li>Press OK</li>\n" +
        		"<li>Choose the volume level you want it to change to.</li>\n" +
        		"<li>Press OK</li>\n" +	
        		"<li>Choose the time of day you want the volume to change back (or to change to a different setting).</li>\n" +
        		"<li>Press OK</li>\n" +
        		"<li>Choose the volume you want it to change to the second time.</li>\n" +
        		"<li>Press OK.</li>\n" +
        		"<li>You will be prompted reminding you that this will control your volume settings and may change your settings immediately based in the timer you set.</li>\n" +
        		"<li>Press OK. The timers will be set.</li></ol>\n" +
        		"<p><b>Q.</b> When I set some timers for tomorrow, it changed by volume right away!</p>\n\n" +
        		"<p><b>A.</b> Please state that in the form of a question.</p>\n\n" +
        		"<p><b>Q.</b> Why did it change my current settings?</p>\n\n" +
        		"<p><b>A.</b> When you set a timer for a time of day earlier than the current time (like for tomorrow morning) the timer will go off immediately and set volume settings. Don't worry, the timer will correctly change the settings tomorrow, too.</p>\n\n" +
        		"<p><b>Q.</b> What if I reboot my phone? Will the timers I set still work?</p>\n\n" +
        		"<p><b>A.</b> They will reinstall upon startup. If you missed one of your timers, it will go off right when you startup.</p>\n\n" +
        		"<p><b>Q.</b> When my ringer is set to 0 volume, it also sets my system volume to 0, why is that?</p>\n\n" +
        		"<p><b>A.</b> If you set your ringer volume to 0, Sound Manager will change modes for you, switching your phone to silent or vibrate, depending on your vibration settings.</p>\n\n" +
        		"<p><b>Q.</b> How do I set a timer to change the volume only once in a day?</p>\n\n" +
        		"<p><b>A.</b> Just set both the \"start\" and \"end\" times to the same time and set the volumes to the same setting.</p>\n\n" +
        		"<p><b>Q.</b> How come I cannot adjust the settings for Email Notification sounds, or others?</p>\n\n" +
        		"<p><b>A.</b> The ability to change, for example, the Email Notification sounds, or other sounds, individually is not exposed to developers. The sounds are controlled by the ringer volume. It's too bad, too, because that's exactly what made me think of creating this application to begin with.</p>\n\n" +
        		"<p>However, the email app does allow you to make notifications silent, but only have it vibrate when new emails come in. You can do this with the IM app, too. Then, set times on the vibrate settings at night to turn vibration off, essentially silencing your emails (and who needs vibrate when you're sleeping anyways?).</p>\n\n" +
        		"<p><b>Q.</b> I had the window open when the volume changed, but the volume controls in the app didn't reflect the change. Why not?</p>\n\n" +
        		"<p><b>A.</b> To reset the volume sliders, just exit out of the app and return.</p>\n\n" +
        		"<p><b>Q.</b> I set my ringmode to 'Vibrate Only', but I returned later to see that it is set to 'Silent', why is that?</p>\n\n" +
        		"<p><b>A.</b> The Vibrate Settings and the Ringmode Toggle can interfere with each other sometimes. If you set the ringmode to 'Vibrate Only', but then went and switched the Ringer Vibration Setting to 'Vibration Off', then the ringmode you chose is no longer valid and should actually be 'Silent'.</p>\n\n" +
        		"<p><b>Q.</b> Why have my ringers not gone off since I updated?</p>\n\n" +
        		"<p><b>A.</b> After an update the Operating System disabled the timers that were in place. A reboot of the phone will quickly reenabled them; otherwise, reset each timer.</p>\n\n" +
        		"<p><b>Q.</b> I muted my phone, but later the volume raised? What's going on?</p>\n\n" +
        		"<p><b>A.</b> The Mute button is a great feature, but it doesn't supersede the timers you set for your volume controls. If you mute your phone, but a timer you set raises the volume, then the volume will be raised.</p>\n\n" +
        		"<p><b>Q.</b> What happened? All my volumes changed at the same time?</p>\n\n" +
        		"<p><b>A.</b> You are able to set a timer on the muting. At the start of the timer, the phone is muted. At that time the volume levels are recorded. At the end of your mute timer, your phone is unmuted and all volumes are returned to their previous value, even if your other volume timers have changed the volume in the meantime. Check your mute timer by choosing the Mute option from the main screen.</p>\n\n" +
        		"<p><b>Q.</b> Help! I created a shortcut to my mute activity, but the shortcut does nothing!</p>\n\n" +
        		"<p><b>A.</b> The shortcut doesn't display anything, but it does toggle your mute status. If you press it once, it mutes your phone. If you press it a second time, the volume levels are returned to what they were when you muted the phone (even if you've changed the volume in the meantime).</p>\n\n" +
        		"</body></html>",
        		"text/html", "utf-8");
    }
}
