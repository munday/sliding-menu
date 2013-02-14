# Dead simple sliding menu
An Android library that extends FragmentActivity to create a menu that slides out from the left hand side of the activity.

## Overview
While it's called SlidingMenu, it can really hold any 2 layouts or fragments. It's pretty easy to implement and has relatively few options at the moment, check the SlidingMenuTest project to see an implementation.

First, you make your Activity extend SlidingMenuActivity like so:

    public class SomeActivity extends SlidingMenuActivity {


Then, the only absolutely required function is to call 

    setLayoutIds(int menuLayoutId, int contentLayoutId)

in your activity's OnCreate before the call to 

    super.OnCreate()

then do everything else like normal.

You can wire up the toggle to any click/touch/etc event by calling 

    toggleMenu();

## Details

### Constructors

    SlidingMenuActivity()

Is the default constructor, this initializes the activity with a hidden title bar (actionbar)
If you want a title bar (actionbar) call:  

    SlidingMenuActivity(bool showTitleBar)

The constructor is the only place to set the showTitleBar option because if it is set after the call to `onCreate()` it won't actually remove the title bar.  
__Note:__ if the title bar is set to show, it does not slide over. It just stays at the top, hence the default setting being to hide it.

### Functions

__setAnimationDuration(long duration)__  
Sets how long in milliseconds the opening/closing animation will run.

__setMaxMenuWidth(int width)__  
Sets the max width (in dips) of the menu. The main content will stop sliding at this many dips from the left side of the screen (unless it would make the content slide farther than the minContentWidth below.

__setMinContentWidth(int width)__  
Sets the minimum amount the content window can leave on the screen. The main content will stop sliding at this many dips from the right side of the screen.

__setAnimationType(int type)__
Sets the type of animation used. Currently there are 2 types:  
    MENU\_TYPE\_SLIDING  
which slides the menu out from the left, pushing the main content over and  
    MENU\_TYPE\_SLIDEOVER  
which is the default, where the content slides away exposing the menu underneath.

__setInterpolator(Interpolator i)__  
Sets the interpolator used by the animation. The default is a DecelerateInterpolator which makes the animation start fast and end slow.


### License
Copyright (c) 2013 Matt Munday

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
