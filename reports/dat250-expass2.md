# DAT250 Experiment Assignment 2 - report

The assignment has for the most part gone fine though I've had some stumbling blocks here and there.

My issues mainly have stemmed from attempting to implement a somewhat strict representation of the domain model given in the assignment, attempting to make do with the classes and variables defined in it. Though eventually I ended up relenting and making some changes and additions as I saw necessary.

## Issues and troubles:

### 1. Polls and referencing other classes
>- **Issue**: The original models for Poll, Vote and VoteOption had no way of knowing/storing who created it, what its VoteOptions were or what polls they belonged to.
>- **Solution(s**): I could either make use of HashMaps in PollManager to store this information or make it so that each individual Poll, Vote and VoteOption stored these values themselves. I ended up choosing the latter as it in my opinion left less of a mess in the PollManager and was easier to implement and keep track of.

### 2. Mapping and entity reference handling
>- **Issue**: Problems with some infinite recursion between references. In addition, issues figuring out the proper mappings and corresponding functions needed
>- **Solution(s)**: Making proper use of the @Json... annotations and taking some time to think and look up possible functions I would need to create, get, alter, and remove the correct values.

### 3. Equality Check Issues
>- **Issue**: Assertion errors occurred when checking for object equality, especially when comparing lists of objects. The `equals()` and `hashCode()` methods were not functioning as expected, leading to failed tests, at times because all they could do was compare the references to the objects when in lists.
>- **Solution(s)**: Overrode the `equals()` and `hashCode()` methods in domain classes (`Poll`, `User`, `Vote`, `VoteOption`) to compare fields rather than relying on reference equality. Adjusted these methods to ensure they were correctly comparing relevant fields and handling edge cases.

### 4. Figuring out HTTP status codes 
>- **Issue**: A learning experience in figuring out what status code / function to use to output from PollController and what I wanted to test on would expect to take in.  
>- **Solution(s)**: Look them up. Fairly sure a few of the .ok()'s could be changed into some error-code when the function does not find what it was looking for, would be happy with some feedback on it as I am a greenhorn at this kind of development.

### 5. URL's, mapping and identifiers
>- **Issue**: In testing I would find that after adding a poll to be stored it would not be at the url it should have been at, f.ex. "/polls/{question}/votes" if making or getting a vote, resulting in no response or en error-code that should not have occurred. The problem lay in me using the question of a poll as an identifier as it seemed that the encoding of the question would make the URL different from what was expected.
>- **Solution(s)**: In order to remedy this issue I decided to implement an ID for polls (a simple Integer for now) as it would avoid some of the encoding issues of a larger String while also being simpler to use for URL's and easier to match on, the same has been done for User.



