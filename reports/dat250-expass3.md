# DAT250 Experiment Assignment 3 - report

[Front-end](../frontend) <br>
[Back-end](../backend)

Not that many issues this time, mostly just took some time to learn/remember svelte/JS, in addition to experimenting a bit with what I can do (The reason my code is a bit of a hodge-podge of code that I was trying out)

## Issues and troubles:

### 1. Vite not working
>- **Issue**: While the project was set up fine in my files I had issues getting the `npm run dev` command to work correctly such that I could view my work in the browser.
>- **Solution(s)**: Switched the project over to a dedicated svelte project with `npm create svelte@latest my-app` and worked from there, I somewhat prefer the setup of this project too.

### 2. Altering backend classes
>- **Issue**: The VoteOption class was in my opinion redundant and could be easily replaced
>- **Solution(s)**: Removed the VoteOption class and instead use Strings (Vote) or a List of Strings (Poll) to keep track of the options of a poll.


### 3. Deserialization issues
>- **Issue**: In the CreatePoll component a list of options was originally sent in as `@RequestBody` to `@PostMapping("/polls")` in order to give the options of the poll. However, this ended up causing some issues with deserialization when in was received by the backend.
>- **Solution(s)**: Make a new `PollCreationRequest` record in the backend to hold the data while in transfer to `@PostMapping("/polls")` so that all the necessary information can be sent as "one" object.



## Possible changes to make:

### Vote counts are currently not finished
>- Currently only showing 0 as all the options result in undefined.

### Backend checks:
>- Currently, the backend is at most only doing the bare minimum in checks on creation of users, polls or the casting of votes and if something with the same username/question already exists. This should be changed to be more strict.

### Showing votes on options:
>- Showing how many votes each option in a poll has on the page when you are making a vote, or after you have cast the vote.

### Expand authentication:
>- The user system on the page is currently very barebones. Could be expanded to have a password or other authentication for a user for each user.

### UI/Layout:
>- Also currently very barebones, though that is mostly something I want to leave for the larger project.

