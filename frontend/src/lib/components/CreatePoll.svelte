<script lang="ts">
    import { currentUser } from '$lib/stores/userStore';
    import { onDestroy } from 'svelte';

    export let addPollToList: Function;

    let question: string = '';
    let options: string[] = [''];
    let validUntil: string = '';

    let errorMessage: string = '';
    let successMessage: string = '';

    let user: { userID: number, username: string, email: string };

    const addOption = () => {
        options = [...options, ''];
    };

    const updateOption = (index: number, value: string) => {
        options = options.map((option, i) => (i === index ? value : option));
    };

    const unsubscribe = currentUser.subscribe(value => {
        user = value;
    });

    onDestroy(() => {
        unsubscribe(); // Clean up the subscription when the component is destroyed
    });

    const createPoll = async () => {
        if (options.filter(o => o.trim()).length < 2) {
            errorMessage = "Poll must have at least two options.";
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/pollApi/polls', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    question: question,
                    userID: user.userID,
                    validUntil: new Date(validUntil).toISOString(),
                    voteOptions: options.filter(o => o.trim())
                }),
            });

            if (response.ok) {
                const newPoll = await response.json(); // Get the newly created poll from the response
                successMessage = 'Poll created successfully!';
                errorMessage = '';

                // Add the new poll to the list
                addPollToList(newPoll);

                // Clear inputs
                question = '';
                options = [''];
                validUntil = '';
            }
            else {
                const errorData = await response.text();
                errorMessage = `Failed to create poll: ${errorData}`;
            }
        }
        catch (error) {
            if (error instanceof Error) {
                errorMessage = `An error occurred: ${error.message}`;
            }
            else {
                errorMessage = 'An unknown error occurred.';
            }
        }
    };
</script>

<main>
    <form on:submit|preventDefault={createPoll}>
        <label for="question">Poll Question:</label>
        <input type="text" id="question" bind:value={question} placeholder="Enter your poll question" required />

        <label for="validUntil">Valid Until:</label>
        <input type="datetime-local" id="validUntil" bind:value={validUntil} required />

        <div>
            <h4>Options:</h4>
            {#each options as option, index}
                <input type="text" bind:value={option} on:input={(e) => updateOption(index, e.target.value)} placeholder={`Option ${index + 1}`} />
            {/each}
            <button type="button" on:click={addOption}>Add Option</button>
        </div>

        <button type="submit">Create Poll</button>
    </form>

    {#if errorMessage}
        <p style="color: red;">{errorMessage}</p>
    {/if}

    {#if successMessage}
        <p style="color: green;">{successMessage}</p>
    {/if}
</main>