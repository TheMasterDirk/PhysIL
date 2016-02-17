FEATURES TO ADD
change scale of map based on TI procedures
toggle b/wn: g = 10 and 9.8
implement wires with 2 snaps for circuitry
rotational joints for things like trebuchets
fields
add notes object to field for explanation purposes
derivatives

BUGS (In random order of relative importance)
1.Flickering. God, the flickering.
2.When saving, add variable components instead of vector sums
3.Snap Joint method doesn't work
4.Issues with Figures + Multiple Variables, Requested SolveFor + Multiple Equations that return answers

UPDATE - 
2600 lines as of Dec. 16th
4765 lines as of Mar. 3rd
5718 lines as of Mar. 29


Things i've changed
-Figure's resize method no longer calls resetImage(vectors show up on big pictures now)
-Figure's rezies method sets base image to user's rezied picture
-Resize Panel (deleted useless method too)
-Varaible things
-Varaible Panel's Add/Change
-Variable Panel Help info + disposing issue
-Graph Panel + Vector Panel Names
-Snap's right click option of 'Delete Object'
-Toolbar's mess of image icons
-Variable Frame messing with VariableName
-CombineVariables mess
-Variable Frame intiallizing for "Change" (and Variables adding a copy of itself)
-Container Dealing with fields
-Toolbar + EP Methods