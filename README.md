# WeCrowd Android

![WeCrowd logo](http://wecrowd.wepay.com/assets/wecrowd-35e0bdd2be0a74e932a5af64576afa02.png "WeCrowd")

## Overview
Native application for [WeCrowd](http://wecrowd.wepay.com/), a sample crowdfunding platform that showcases easy payment transactions courtesy of the WePay Android SDK and [API](https://www.wepay.com/developer/). *Note that WeCrowd does not actually process any payments*.

## What the app is for
* The app demonstrates donation flow from two perspectives: a merchant and a payer. A merchant can accept donations via a card swiper or [virtual terminal] (https://en.wikipedia.org/wiki/Virtual_terminal). A virtual terminal is basically a way for someone (presumably a merchant) to enter credit card information on behalf of someone else (presumably a payer).
* The app is meant to be an implementation guide of sorts for platforms integrating the WePay Android SDK. Best Android development practices have been used as much as possible.

## What the app is NOT for
* This app does not perform actual transactions or donate to any real causes.
* This app is not meant to be a full-fledged version of the WeCrowd web app. Some functionality (such as creating a campaign as a merchant) is not implemented since the primary goal of the app is to showcase the payment transaction flow.

## External code used
Huge shout out to these libraries!
* [Android Signature Pad](https://github.com/gcacace/android-signaturepad) [License](http://www.apache.org/licenses/LICENSE-2.0.txt)
* WePay Android SDK

## Usage Instructions
1. Clone the repo
2. Change to the repo root directory --  `$ cd ./wepay-wecrowd-mobile-android/`
4. Ensure [Android Studio](https://developer.android.com/sdk/index.html) is installed
5. Open the project in Android Studio
6. Ensure Android studio device settings are configured to your liking -- **Run->Edit Configurations**
7. Build and run the app on your favorite Android device (it may take a few minutes the first time)!

## License
See the [LICENSE](LICENSE) file for license rights and limitations (MIT).
