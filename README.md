# StudyBuddy
-----------
This is a sample Android app that I made to try and get a feel of how trying to learn a new framework would be.

You can set objectives inside the app, with a starting and an ending date (eg: Read 60 pages from today to 10 days from now).
The app tells you, for example, how many pages per day you should read in order to achieve your goal in that set time, and updates
the daily count based on whether you completed your daily objective the day before.

## App Layout

### UI Layer
The UI Layer consists of three activities:

- `MainActivity.java`
- `ObjectiveCreationActivity.java`
- `Settings.java`

Apart from MainActivity.java (which displays active objectives), the other ones
are pretty self explanatory.
There is also an adapter used in `MainActivity.java` by a `RecyclerView`.

These activities communicate with the data layer through the use of a `ViewModel` (`ObjectiveViewModel.java`).

### Data Layer
The Data Layer code can be found inside the `Objectives` folder.

There is a basic class, named `Objective.java`, which represents the objectives set by the user.
Using this as a base, I implemented a `ViewModel`, a Database (using [Android Room](https://developer.android.com/training/data-storage/room)), and other helper classes used by these basic functions (Enum for Objectives, Type Converters, Data Acess Object, etc.)

### "Android" Layer
In order for the app to be able to send daily notifications, and to reset the 'daily completion' state of the objectives,
an alarm needs to be set using `AlarmManger`.\
This Alarm, in turn, needs to be received by a `BroadcastReceiver`, which in this case is `AlarmReceiver.java`.

First, this class identifies the type of alarm that has fired off, and then sends the appropiate message to `MainActivity.java`, 
which is where the implementations of the responses can be found.

#### Other remarks
Instrumentation tests can be found inside the `androidTest` folder.
I didn't write any unit tests because, honestly, I didn't feel like it.

The app is translated in both English and Spanish, and resource files can be found inside
the `res` folder.

----------- 

#### Notes

This is not meant to be a real and fully working app, it's just a side project I did for fun.
