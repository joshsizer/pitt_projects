/**
 *
 * Author: Joshua Sizer (jas625)
 *
 **/

#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

const char* dictionaryFile = "dictionary.txt";

typedef enum {
  WIN,
  CORRECT_GUESS,
  STRIKE,
} GUESS_RESULT;

// wrapper function for fgets
void get_line(char* buffer, int size, FILE* f) {
  fgets(buffer, size, f);

  int strlength = strlen(buffer);
  if (strlength != 0 && buffer[strlength - 1] == '\n') {
    buffer[strlength - 1] = '\0';
  }
}

// returns 1 if the two strings are equal, and 0 otherwise.
int streq(const char* a, const char* b) { return strcmp(a, b) == 0; }

// returns 1 if the two strings are equal ignoring case, and 0 otherwise.
// so "earth" and "Earth" and "EARTH" will all be equal.
int streq_nocase(const char* a, const char* b) {
  // hohoho aren't I clever
  for (; *a && *b; a++, b++)
    if (tolower(*a) != tolower(*b)) return 0;
  return *a == 0 && *b == 0;
}

// gets the number of words in our dictionary file, assuming the first line
// indicates how many words are in the dictionary.
int get_num_words(FILE* dict) {
  // create a string to store the first line
  char first_line[20];
  // use wrapper function for fgets
  get_line(first_line, sizeof(first_line), dict);

  // convert from string to number
  int num_words = atoi(first_line);

  // when atoi cannot convert, it returns 0.
  if (num_words == 0) {
    fprintf(stderr, "ERROR: dictionary has no words!");
    return -1;
  }
  return num_words;
}

// reads each word in the dictionary to an index in words_arr
void read_dictionary(FILE* dict, char words_arr[][20], int num_words,
                     int word_size) {
  int words_read = 0;

  while (!feof(dict)) {
    char line_buffer[word_size];
    get_line(line_buffer, sizeof(line_buffer), dict);
    strcpy(words_arr[words_read++], line_buffer);
  }

  if (words_read != num_words) {
    printf("Reached end of file before reading all %d words\n", num_words);
  }
}

// returns a random number in range [low_value, high_value)
int random_range(int low_value, int high_value) {
  return rand() % (high_value - low_value) + low_value;
}

// returns a random word from the dictionary
char* get_random_word(char words_arr[][20], int num_words) {
  int random_number = random_range(0, num_words);
  return words_arr[random_number];
}

// initializes the guesses arr to be full of underscores (_)
void initialize_guesses(char* arr, int size) {
  for (int i = 0; i < size; i++) {
    arr[i] = '_';
  }
  arr[size] = '\0';
}

// prints the users guesses
void print_game(char arr[], int size) {
  for (int i = 0; i < size; i++) {
    printf("%c ", arr[i]);
  }
  printf("Guess a letter or type the whole word: ");
}

// checks the guesses array against the user's input and updates
// the guess accordingly, returning the status of the guess
// (WIN, CORRECT_GUESS, STRIKE)
GUESS_RESULT update_guesses(char guesses[], int guesses_size, char user_input[],
                            int user_in_size, char word[]) {
  int num_replaced = 0;

  if (user_in_size == 1) {
    char char_guess = user_input[0];
    for (int i = 0; i < guesses_size; i++) {
      char cur_let = word[i];
      if (cur_let == char_guess) {
        guesses[i] = cur_let;
        num_replaced++;
      }
    }
  }

  if (streq_nocase(word, user_input) || streq_nocase(guesses, word)) {
    return WIN;
  } else if (num_replaced > 0) {
    return CORRECT_GUESS;
  } else {
    return STRIKE;
  }
}

int main(int argc, char* argv[]) {
  // seed random number generator
  srand((unsigned int)time(NULL));

  // attempt to open our dictionary file
  FILE* dict = fopen(dictionaryFile, "r");
  if (dict == NULL) {
    fprintf(stderr, "ERROR: could not open %s\n", dictionaryFile);
    return 1;
  }

  // read in the number of words in the file
  int num_words = get_num_words(dict);
  // create an array of strings to hold our words.
  char words_arr[num_words][20];
  // read each word into the array
  read_dictionary(dict, words_arr, num_words, 20);
  // close the dictionary file
  fclose(dict);

  // uncomment to print out all words
  // for (int j = 0; j < num_words; j++) {
  //   printf("%s\n", words_arr[j]);
  // }

  char* random_word = "";

  if (argc > 1) {
    random_word = argv[1];
  } else {
    random_word = get_random_word(words_arr, num_words);
  }

  int str_len = strlen(random_word);
  printf("Welcome to hangman! Your word has %d letters.\n", str_len);

  char guesses[str_len + 1];
  initialize_guesses(guesses, str_len);

  int num_strikes = 0;
  // game loop
  while (num_strikes < 5) {
    print_game(guesses, str_len);
    // get user guess
    char user_input[50];
    get_line(user_input, sizeof(user_input), stdin);
    GUESS_RESULT result = update_guesses(guesses, str_len, user_input,
                                         strlen(user_input), random_word);

    switch (result) {
      case WIN:
        printf("You got it! The word was '%s.'\n", random_word);
        return 0;
      case STRIKE:
        num_strikes++;
        printf("Strike %d!\n", num_strikes);
      case CORRECT_GUESS:
        break;
    }
  }

  printf("Sorry, you lost! The word was '%s.'\n", random_word);
  return 0;
}
