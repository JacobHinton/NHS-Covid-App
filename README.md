# NHS-Covid-App-Team-Project
An app that visualises the cases and deaths of people who have had covid-19 and predicts future cases based on earlier data


All required libraries are in the `project/lib` folder. The libraries
can alternatively be downloaded via Maven

## Libraries used

- jfreechart
- pdfbox-2.0.24
- fontbox-2.0.24
- pdfbox-app-2.0.25
- junit.jupiter

## How to run from source

The main function is located within `src/gui/MainGUI.java` and program execution is expected to begin here.

On application launch, the program will create a `resources` folder, which will be home to the cached CSV file it will
automatically download on first launch, as well as any PDF files generated.
