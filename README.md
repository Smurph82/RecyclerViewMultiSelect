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
dependencies {te
    ...
    compile 'com.smurph.multiselectlib:multiselectlib:1.0.9'
}
```

##### COMING SOON
* Load the Helper with already selected items/position.
* Add inline Java Docs
* ~~New demo app showing all the features and examples.~~
* ~~Custom RecyclerView Adapter class to implement some of the function for the end developer.~~
* ~~MultiSelect w/o CAB.~~
* ~~Single select mode w/ CAB.~~
* ~~Single select mode w/o CAB.~~

##### KNOWN ISSUES
* With the ViewPager example if there are not enough items to fill the screen the swipe to refresh and the Collapsing Toolbar do not work all the time. This is an issue with the Collapsing Toolbar Android support library and not the MultiSelectHelper library.
* ~~Under version 1.0.5 removing an item from the RecyclerView will throw off the positioning of this library. I look to have a fix in version 1.0.6~~
* ~~Under version 1.0.5 there is not direct way of getting the selected item count. Should be in version 1.0.6~~

##### Looking into
* See if the new, and still beta, Android Data Binding is an option for this library.
