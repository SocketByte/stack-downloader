# StackOffline Downloader
StackOffline Downloader uses JSoup to download questions from [StackOverflow](https://stackoverflow.com)

You can also download a pre-made package:

[Click here to download: Java tag, approx. 700.000 questions, 1.2GB](http://www.mediafire.com/file/msrp97mhlm7l97q/data.zip/file)

It took approximately 18 days to fetch. So yeah, it's not something you can do in
one sitting.

StackOffline Downloader is feature-rich and safe for both sides. It automatically
removes corrupted or invalid data.

It also limits rate of download to the max allowed rate of Stackoverflow site, because
otherwise you could get banned for using more data than allowed too fast.

### Installation
Just download `stack-downloader-1.0-SNAPSHOT` file from the Releases tab.
And put it onto `user/stack/` folder. Why `stack` folder? Because it will be 
easier to zip it later.

### Configuration
You need to open the executable with few important arguments.
```batch
@echo off
title StackOffline Data Downloader
java -jar stack-downloader-1.0-SNAPSHOT.jar EMAIL PASSWORD TAG SORT_TYPE PAGESIZE
pause
```
where:
* **EMAIL** - email to authorize to StackOverflow
* **PASSWORD** - password to authorize to StackOverflow
* **TAG** - tag you want to download, for example `cpp`, `java`, `kotlin`, `c`
* **SORT_TYPE** - sort type, available: `sorted`, `featured`, `frequent`, `votes`, `active`
* **PAGESIZE** - questions to be fetched per page, recommended: 50, max: 50
##### This is for Windows with batch file support only, you can create similar using Linux Shell

Preferably, put this program onto your VPS on an independent screen, 
because the fetch can take some time because of StackOverflow ratelimit.
50 questions fetch around 1 minute. So do your math and deduce how much is to
 download a whole C++/C tag :)
 
Stack Downloader will download the files onto `/data/` folder.
After you feel like it's enough of questions, stop the program, 
and assuming you have `/%user%/stack/data`, execute these commands:
```
sudo apt update
sudo apt install zip
sudo zip -r data.zip stack/data
```
It will create a `*.zip` file with `stack/data/*.json` structure in it. The structure is very important, currently!

### License
Project is fully open-source and licensed under MIT License