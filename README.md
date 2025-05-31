# Calendar application

## How to run the program

1. Unzip the project folder `project.zip`
2. Open the terminal
3. Navigate to the unzipped folder `project`
4. Navigate to the `res` folder.
5. It should contain the following files: `CalendarApp.jar`, `commands.txt`, `error_commands.txt`
   `June2025.csv` and `GoogleCalendar-June2025.png`.
6. You can change the export file name in the `commands.txt` file. to generate a new file.
7. Run the following command in the terminal: `java -jar CalendarApp.jar --mode headless 
commands.txt` for headless mode.
8. Run the following command in the terminal: `java -jar CalendarApp.jar --mode interactive` for
   interactive mode.
9. Run the following command in the terminal: `java -jar CalendarApp.jar` for
   GUI mode (or double-click on the jar file).
10. For testing, make sure the `scripts` folder is in the root directory along with src, test
    and res
    folders.
11. After setting up your maven project, the folder structure should look like this:

    - project
        - res
        - README.md
        - USEME.md
        - scripts
          - scripts to test the app in headless mode and test import/export functionality
        - images
          - images for the useme Markdown file
        - src
            - main
                - java
                    - controller
                    - model
                    - view
                    - utils
                    - CalendarApp.java
        - test
            - java
                - All test files


## Features

### Create Event

- **Single Event**:
  `create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString>`
- **Single Event with AutoDecline**:
  `create event --autoDecline <eventName> from <dateStringTtimeString> to <dateStringTtimeString>`
- **Recurring Event (N times)**:
  `create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> for <N> times`
- **Recurring Event (until date)**:
  `create event <eventName> from <dateStringTtimeString> to <dateStringTtimeString> repeats <weekdays> until <dateStringTtimeString>`
- **All Day Event**: `create event <eventName> on <dateString>`
- **All Day Event with AutoDecline**: `create event --autoDecline <eventName> on <dateString>`
- **Recurring All Day Event (N times)**:
  `create event <eventName> on <dateString> repeats <weekdays> for <N> times`
- **Recurring All Day Event (until date)**:
  `create event <eventName> on <dateString> repeats <weekdays> until <dateString>`

- **Note**: Create has reserved keywords for location, description, --autoDecline and --public.
  All created events are private by default. The name, location and description can also have
  multiple words but must follow this syntax ... <keyword> "firstWord .... lastWord". Note that
  the first word must start with a double quote and the last word must end with a double quote.
  Eg: `create event "Multi word evName" from 2021-10-10T10:00 to 2021-10-10T11:00 location "this is a 
loc" description "this is a desc" --public`
- The reserved keywords are: `from`, `to`, `repeats`, `on`, `for`, `until`, `location`,
  `description`, `--autoDecline`, `--public`.
  And should not be used even in quotes.

**Note**: for a recurring event the --autoDecline flag is always on.

### Edit Event

- **Edit Single Event**:
  `edit event <property> <eventName> from <dateStringTtimeString> to <dateStringTtimeString> with <NewPropertyValue>`
    - We can edit a single event by specifying the property we want to change or edit a single
      instance of a recurring event.
- **Edit Multiple Events (from specific date/time)**:
  `edit events <property> <eventName> from <dateStringTtimeString> with <NewPropertyValue>`
- **Edit Multiple Events**: `edit events <property> <eventName> <NewPropertyValue>`

**Note**: The property can be one of the following: `name`, `location`, `description`,
`startDateTime`,
`endDateTime`, `frequency`, `weekDays`, `untilDateTime`, `from` and `to`. The property value can
also have multiple words but must follow the same syntax as the create event command.

**Note**: When editing multiple events, the fromDateTime is used to match the events that start from
that time, it will match any event (single or some occurrence of a recurring event) that starts
following that time.

**Note**: When we are editing one of the complex properties of a recurring event (i.e.
untilDateTime, frequency, startDateTime, weekDays), the event sequence is regenerated instead of
edited aligning with how google calendar events handle these cases. For example, if we change
have 10 occurrences of an event and we change go to the 5th occurrence and change the
frequency to 2. The app will delete the the following 6 occurrences(5 + 1 this) and create 2 new
occurrences.

### Print Events

- **Print Events on Specific Date**: `print events on <dateString>`
- **Print Events in Date Range**:
  `print events from <dateStringTtimeString> to <dateStringTtimeString>`

### Export Calendar

- **Export Calendar to CSV**: `export cal fileName.csv`

### Show Status

- **Show Busy Status**: `show status on <dateStringTtimeString>`
- **Show Busy Status in Date Range**:
  `show status from <dateStringTtimeString> to <dateStringTtimeString>`

### Create Calendar

`create calendar --name <calName> --timezone area/location`
Adds a new calendar with the given name and timezone. There should be only one calendar with the
same name. The timezone should be in the IANA Time Zone Database format.
Eg: `create calendar --name "My Cal" --timezone Asia/Kolkata`
or `create calendar --name NewCalendar --timezone America/New_York`

### Edit Calendar

`edit calendar --name <name-of-calendar> --property <property-name> <new-property-value>`

- **Edit Calendar Name**: `edit calendar --name <name-of-calendar> --property name <new-name>`
- **Edit Calendar Timezone**:
  `edit calendar --name <name-of-calendar> --property timezone <new-timezone>`

Make sure the new name is unique and the calendar exists. The timezone should be in the IANA Time
Zone Database format.

### Use Calendar

`use calendar --name <name-of-calendar>`

- **Use Calendar**: `use calendar --name <name-of-calendar>`
  Makes the calendar with the given name the active calendar. The calendar should exist.
- A user can create/edit/print/export events in the context of a calendar. They can use this command
  to set the calendar context.

### Copy Events

- `copy event <eventName> on <dateStringTtimeString> --target <calendarName> 
to <dateStringTtimeString>`

The command is used to copy a specific event with the given name and start date/time from the
current calendar to the target calendar to start at the specified date/time. The "to" date/time is
assumed to be specified in the timezone of the target calendar. The copied event must be in the same
timezone as the target calendar.

- `copy events on <dateString> --target <calendarName> to <dateString>`

This command has the same behavior as the copy event above, except it copies all events scheduled on
that day. The times remain the same, except they are converted to the timezone of the target
calendar.

- `copy events between <dateString> and <dateString> --target <calendarName> to <dateString>`

The command has the same behavior as the other copy commands, except it copies all events scheduled
in the specified date interval. The date string in the target calendar corresponds to the start of
the interval. The endpoint dates of the interval are inclusive.

# Architecture

We have used the MVC architecture for this project:

- **Model**: Manages data and business logic.
- **View**: Handles data presentation to the user (terminal in our case).
- **Controller**: Processes user input and updates the Model and View accordingly.

### Model

The Model manages data and business logic.

- **CalendarModel**
    - Interface to manage multiple `SingleCalendar` objects.
    - Contains a map where:
        - **Key**: Name of the calendar.
        - **Value**: A pair of timezone string and `SingleCalendar` object.
    - Provides methods to:
        - Create, edit, and manage calendars.
        - Copy single and multiple events between calendars.
    - Maintains the currently active calendar.
    - BasicCalendarModel implements this interface. It creates a default calendar and sets it as the
      active calendar. This default calendar has the name "default" and timezone as the timezone
      of the system (America/New_York for our case).

- **SingleCalendar**
    - Abstract class represents an individual calendar.
    - Contains a list of `CalendarEntry` objects.
    - Provides methods to:
        - Create and edit single/recurring events.
        - Print events.
        - Export the calendar.
        - Display calendar status.

- **CalendarEntry**
    - Abstract class representing a calendar entry.
    - Fields include:
        - `name`, `location`, `description`.
        - `start` and `end` date/time.
        - `isPrivate` and `isAllDay` attributes.
    - Declares abstract methods for concrete event classes.

- **SingleEvent**
    - Represents a single event within the calendar system.
    - Extends `CalendarEntry`.

- **RecurringEvent**
    - Represents a recurring event.
    - Extends `CalendarEntry`.
    - Contains a list of `CalendarEntry` objects representing occurrences.
    - Provides methods to:
        - Add, remove, and edit recurring event occurrences.

### Controller

The Controller handles user input.

- Parses user commands and calls the relevant Model methods.
- Uses the **Command Design Pattern** to manage different user commands.
- Maintains a map of command objects:
    - **Key**: Command name. (The first word in the user input)
    - **Value**: Corresponding command object.
- Supports both interactive and headless modes.

### Gui Controller

This is a new implementation of the controller interface to set up the GUI. It is used to initialize
the features object and set up the GUI. It is also used to set up the calendar manager and the
single calendar.

### Gui Features

This is a new implementation of the features interface to set up the GUI. It helps in isolating
the view and model.

### View

The View is responsible for displaying data to the user.

- The terminal serves as the View.
- **BasicCalendarView**
    - Uses an `Appendable` object to write output to the terminal.

### Gui View

- **GuiCalendarView**
    - Implements the `GuiView` interface.
    - Use the javax swing and awt libraries to create the GUI.
    - Displays the calendar and events in a user-friendly manner.
    - Uses the features object to get the data from the model and display it in the GUI.

### Utils

Provides common static methods that help in parsing and formatting data.

- **DateTimeUtils**
    - Provides methods to parse and format date and time strings.

- **ParsingUtils**
    - Provides methods to parse user input strings.

## Changes from last Assignment

- **Model**:
1. Our model now returns the event details in a `Map<String, String>` format instead of a
   `String` format.
   - This allows us to add new properties in the future without changing the method signature.
   - Also the controller can then decide how to process the key value pairs according to the
     requirements of view.
2. Added method to get the current calendar name and Map of all calendars in the system.
   - This helps in populating calendar dropdown in the gui view. 

- **Controller**:
1. Created a new implementation of the controller interface to set up the GUI 
   (`GuiCalendarController`).
2. Created new dedicated interfaces and implementations classes for importing and exporting the 
   calendar csv files. 
   - This will help us to support new types of files in the future.
   - Isolated the reading and writing of files from the main controller.
3. Created new interface for `Features` and implemented it in the `GuiFeatures` class.
   - This class is responsible for handling the features of the calendar.
   - It uses the `CalendarModel` interface to manage the calendars and events.
   - It also uses the `GuiView` interface to display the data to the user.

- **View**:

1. Created a new interface for `GuiView` and implemented it in the `GuiCalendarView` class.
2. The `GuiCalendarView` class is responsible for displaying the GUI and handling user input.
   It uses the javax swing and awt libraries to create the GUI.
3. Use a component-based approach to create the GUI.
   - This allows us to create a modular and reusable GUI.
   - The GUI is created using the `JFrame`, `JPanel`, `JButton` and `JDialog` classes.
   - The GUI is designed to be user-friendly and easy to use.

## Division of Work

- **Shrijan S Shetty** - Design and implementation of the Model and testing.
- **Hrishikesh Pradhan** - Design and implementation of the Controller & View along with testing.
