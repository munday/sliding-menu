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

Is the default constructor, this initializes the activity with a sliding title bar (actionbar)
If you want to pick if the title bar (actionbar) slides or not call:

    SlidingMenuActivity(bool slideTitleBar)

If you want the titlebar (actionbar) to disapper, call `requestWindowFeature(Window.FEATURE_NO_TITLE);` in your `onCreate()`.


### Functions

    setAnimationDuration(long duration)  
Sets how long in milliseconds the opening/closing animation will run.

    setMaxMenuWidth(int width)  
Sets the max width (in dips) of the menu. The main content will stop sliding at this many dips from the left side of the screen (unless it would make the content slide farther than the minContentWidth below.

    setMinContentWidth(int width)  
Sets the minimum amount the content window can leave on the screen. The main content will stop sliding at this many dips from the right side of the screen.

    setAnimationType(int type)  
Sets the type of animation used. Currently there are 3 types:  
    MENU\_TYPE\_SLIDING  
which slides the menu out from the left, pushing the main content over and  
    MENU\_TYPE\_SLIDEOVER  
which is the default, where the content slides away exposing the menu underneath. And finally, the experimental
    MENU\_TYPE\_PARALLAX
which sounds all fancy, but just means that the menu and content slide at different speeds.


    setInterpolator(Interpolator i)  
Sets the interpolator used by the animation. The default is a DecelerateInterpolator which makes the animation start fast and end slow.


### License
Copyright (c) 2013 Matt Munday

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

