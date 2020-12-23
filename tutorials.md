##### Advanced Android Tutorial (Fragments & Bottom Navigation Buttons)
https://www.youtube.com/watch?v=B_10GhBgt10&list=PLgCYzUzKIBE-1BsxlQTIPF7B95ey34jUF
##### Changing API Requirements (Minimum, Target, Max)
http://www.apnatutorials.com/android/how-to-change-api-level-in-android-studio.php?categoryId=2&subCategoryId=59&myPath=android/how-to-change-api-level-in-android-studio.php
##### MP Android Charts
https://www.youtube.com/watch?v=iS7EgKnyDeY
https://github.com/PhilJay/MPAndroidChart
##### Multiple List Items (i.e. ViewHolders) in the Same RecyclerView
https://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView
https://guides.codepath.com/android/Using-the-RecyclerView
##### Settings Screens
https://www.youtube.com/watch?v=PS9jhuHECEQ
##### Top Bar Buttons
https://www.youtube.com/watch?v=5MSKuVO2hV4&feature=youtu.be&t=69
https://stackoverflow.com/questions/33329362/how-to-use-buttons-on-action-bar-with-fragments



##### Debugging the database (windows)
- Run emulator from android studio
- Open Command Prompt
- Type 'adb -e shell'
- Type 'su'
- You should see the $ change to a #
- Type 'cd data/data/com.neu.habify/databases'
- Type 'sqlite3 habify_database'
- Now you can run regular sql queries on the database in real time with the emulator
- Try '.tables' to see a list of tables, or 'select * from users' after doing profile setup
- Ctrl-D to quit adb stuff, sometimes? Or just close command prompt if it gives you a hard time
- If you restart or reinstall the app, you may need to do this process again
