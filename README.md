# RecyclerViewMultiSelect
This is a simple implementation of a MultiSelect with ActionMode for the RecyclerView. It is meant to help use the CAB with the RecyclerView.

Add to Android Studio project
```
buildscript {
    repositories {
        jcenter()
    }
    ...
}
```

```
dependencies {
    ...
    compile 'com.smurph.multiselectlib:multiselectlib:1.0.6'
}
```

##### COMING SOON
* ~~New demo app showing all the features and examples.~~
* ~~Custom RecyclerView Adapter class to implement some of the function for the end developer.~~
* Load the Helper with already selected items/position.
* ~~MultiSelect w/o CAB.~~
* ~~Single select mode w/ CAB.~~
* ~~Single select mode w/o CAB.~~

##### KNOWN ISSUES
* ~~Under version 1.0.5 removing an item from the RecyclerView will throw off the positioning of this library. I look to have a fix in version 1.0.6~~
* ~~Under version 1.0.5 there is not direct way of getting the selected item count. Should be in version 1.0.6~~

##### Looking into
* See if the new, and still beta, Android Data Binding is an option for this library.
