<script lang="ts">
    import { onMount } from 'svelte';
    import { goto } from '$app/navigation';
    import { currentUser } from '$lib/stores/userStore'; // Import the store
    import { onDestroy } from 'svelte';

    export let pollID: number;

    let poll: any = null;
    let voteCounts: Record<string, number> = {}; // Initialize with an empty record
    let selectedOption: string = '';

    let errorMessage: string = '';
    let successMessage: string = '';

    let user: { userID: number, username: string, email: string };

    const unsubscribe = currentUser.subscribe(value => {
        user = value;
    });

    onDestroy(() => {
        unsubscribe(); // Clean up the subscription when the component is destroyed
    });

    const fetchPoll = async () => {
        try {
            const response = await fetch(`http://localhost:8080/pollApi/polls/${pollID}`);
            if (response.ok) {
                poll = await response.json();
                await fetchVoteCounts(); // Fetch vote counts after fetching the poll
                errorMessage = '';
            } else {
                errorMessage = 'Poll not found';
            }
        } catch (error: unknown) {
            if (error instanceof Error) {
                errorMessage = `Failed to fetch poll: ${error.message}`;
            } else {
                errorMessage = 'An unknown error occurred while fetching the poll.';
            }
        }
    };

    const fetchVoteCounts = async () => {
        try {
            const response = await fetch(`http://localhost:8080/pollApi/polls/${pollID}/voteCounts`);
            if (response.ok) {
                voteCounts = await response.json();
                errorMessage = '';
            } else {
                errorMessage = 'Failed to fetch vote counts';
            }
        } catch (error: unknown) {
            if (error instanceof Error) {
                errorMessage = `Failed to fetch vote counts: ${error.message}`;
            } else {
                errorMessage = 'An unknown error occurred while fetching vote counts.';
            }
        }
    };

    const submitVote = async () => {
        if (!selectedOption) {
            errorMessage = 'You must select an option to vote.';
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/pollApi/polls/${pollID}/votes?userID=${user.userID}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(selectedOption),
            });

            if (response.ok) {
                successMessage = 'Vote submitted successfully!';
                errorMessage = '';
                await goto('/');
            } else {
                const errorData = await response.text();
                errorMessage = `Failed to submit vote: ${errorData}`;
            }
        } catch (error: unknown) {
            if (error instanceof Error) {
                errorMessage = `An error occurred: ${error.message}`;
            } else {
                errorMessage = 'An unknown error occurred while submitting your vote.';
            }
        }
    };

    onMount(() => {
        fetchPoll();
    });
</script>

<main>
    {#if poll}
        <h2>{poll.question}</h2>
        <form on:submit|preventDefault={submitVote}>
            <h4>Options (Ignore votes, currently bugged):</h4>
            {#each poll.voteOptions as option}
                <div>
                    <label>
                        <input type="radio" name="option" value={option} bind:group={selectedOption} />
                        {option} ({voteCounts[option] || 0} votes)
                    </label>
                </div>
            {/each}

            <button type="submit">Submit Vote</button>
        </form>

        {#if errorMessage}
            <p style="color: red;">{errorMessage}</p>
        {/if}

        {#if successMessage}
            <p style="color: green;">{successMessage}</p>
        {/if}
    {:else if errorMessage}
        <p>{errorMessage}</p>
    {/if}
</main>

<style>
    form {
        margin-top: 1rem;
    }

    label {
        display: block;
        margin: 0.5rem 0;
    }

    button {
        margin-top: 1rem;
    }
</style>