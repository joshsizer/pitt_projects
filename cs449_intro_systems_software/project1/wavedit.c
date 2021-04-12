/**
 * Author: Joshua Sizer (jas625)
 * Disgregard the big-little endian conversions. I left them here
 * because I went through the trouble of doing the conversions when
 * I mistakenly compiled on unixs, and now my program is big and
 * little endian compatable.
 **/

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct WAVHeader {
  char riff_id[4];
  uint32_t file_size;
  char wave_id[4];
  char fmt_id[4];
  uint32_t fmt_size;
  uint16_t data_format;
  uint16_t number_of_channels;
  uint32_t samples_per_second;
  uint32_t bytes_per_second;
  uint16_t block_alignment;
  uint16_t bits_per_sample;
  char data_id[4];
  uint32_t data_size;
} WAVHeader;

unsigned short switch_endianess_uint16(unsigned int num) {
  return (num >> 8) | (num << 8);
}

unsigned int switch_endianess_uint32(unsigned int num) {
  return ((num >> 24) & 0xff) |       // move byte 3 to byte 0
         ((num << 8) & 0xff0000) |    // move byte 1 to byte 2
         ((num >> 8) & 0xff00) |      // move byte 2 to byte 1
         ((num << 24) & 0xff000000);  // byte 0 to byte 3
}

void convert_header_endianess(WAVHeader* header) {
  header->file_size = switch_endianess_uint32(header->file_size);
  header->fmt_size = switch_endianess_uint32(header->fmt_size);
  header->data_format = switch_endianess_uint16(header->data_format);
  header->number_of_channels =
      switch_endianess_uint16(header->number_of_channels);
  header->samples_per_second =
      switch_endianess_uint32(header->samples_per_second);
  header->bytes_per_second = switch_endianess_uint32(header->bytes_per_second);
  header->block_alignment = switch_endianess_uint16(header->block_alignment);
  header->bits_per_sample = switch_endianess_uint16(header->bits_per_sample);
  header->data_size = switch_endianess_uint32(header->data_size);
}

int is_big_endian() {
  int n = 1;
  return (*(char*)&n) != 1;
}

// print the help message for this program
void print_help_msg() {
  printf(
      "usage: wavedit [file]\n       \t--Displays information about the wav "
      "file.\n");
  printf(
      "       wavedit [file] -rate [rate]\n       \t--Changes the declared "
      "bitrate of "
      "the wav file, effectively slowing or reversing the file. Rate must be "
      "in the range (0, 192000]\n");
  printf("       wavedit [file] -reverse\n       \t--Reverses the wav file.\n");
}

FILE* open_file(char* path) { return fopen(path, "rb+"); }

int file_exists(FILE* file) {
  if (file == NULL) {
    return 0;
  }
  return 1;
}

WAVHeader read_header(FILE* file) {
  WAVHeader header;
  fread(&header, sizeof(header), 1, file);

  // wav is little endian
  if (is_big_endian()) {
    convert_header_endianess(&header);
  }
  return header;
}

int is_wav_file(WAVHeader* header) {
  if (strncmp(header->riff_id, "RIFF", 4) != 0) {
    return 0;
  }
  if (strncmp(header->wave_id, "WAVE", 4) != 0) {
    return 0;
  }
  if (strncmp(header->fmt_id, "fmt ", 4) != 0) {
    return 0;
  }
  if (strncmp(header->data_id, "data", 4) != 0) {
    return 0;
  }
  if (header->fmt_size != 16) {
    return 0;
  }
  if (header->data_format != 1) {
    return 0;
  }
  if (header->number_of_channels != 1 && header->number_of_channels != 2) {
    return 0;
  }
  if (header->samples_per_second <= 0 || header->samples_per_second > 192000) {
    return 0;
  }
  if (header->bits_per_sample != 8 && header->bits_per_sample != 16) {
    return 0;
  }
  if (header->bytes_per_second != header->samples_per_second *
                                      (header->bits_per_sample) / 8 *
                                      header->number_of_channels) {
    return 0;
  }
  if (header->block_alignment !=
      (header->bits_per_sample) / 8 * header->number_of_channels) {
    return 0;
  }

  return 1;
}

void print_wav_info(WAVHeader* header) {
  char* channels;
  if (header->number_of_channels == 1) {
    channels = "mono";
  } else {
    channels = "stereo";
  }
  unsigned int sample_length = header->data_size / header->block_alignment;
  float length_in_sec = sample_length / (float)header->samples_per_second;
  printf("This is a %u-bit %uHz %s sound.\n", header->bits_per_sample,
         header->samples_per_second, channels);
  printf("It is %u samples (%.3f seconds) long.\n", sample_length,
         length_in_sec);
}

void reverse_int8_arr(int8_t* arr, int size) {
  for (int i = 0; i < (size / 2); i++) {
    int8_t trans = arr[i];
    arr[i] = arr[size - 1 - i];
    arr[size - 1 - i] = trans;
  }
}

void reverse_int16_arr(int16_t* arr, int size) {
  for (int i = 0; i < (size / 2); i++) {
    int16_t trans = arr[i];
    arr[i] = arr[size - 1 - i];
    arr[size - 1 - i] = trans;
  }
}

void reverse_int32_arr(int32_t* arr, int size) {
  for (int i = 0; i < (size / 2); i++) {
    int32_t trans = arr[i];
    arr[i] = arr[size - 1 - i];
    arr[size - 1 - i] = trans;
  }
}

void reverse_wav(WAVHeader* header, FILE* file) {
  unsigned int sample_length = header->data_size / header->block_alignment;
  uint16_t num_chan = header->number_of_channels;
  uint16_t bits_per_samp = header->bits_per_sample;
  if (bits_per_samp == 8 && num_chan == 1) {
    int8_t data_arr[sample_length];
    fread(data_arr, sizeof(data_arr), 1, file);
    reverse_int8_arr(data_arr, sample_length);
    fseek(file, sizeof(WAVHeader), SEEK_SET);
    fwrite(data_arr, sizeof(data_arr), 1, file);
  } else if ((bits_per_samp == 8 && num_chan == 2) ||
             (bits_per_samp == 16 && num_chan == 1)) {
    int16_t data_arr[sample_length];
    fread(data_arr, sizeof(data_arr), 1, file);
    reverse_int16_arr(data_arr, sample_length);
    fseek(file, sizeof(WAVHeader), SEEK_SET);
    fwrite(data_arr, sizeof(data_arr), 1, file);
  } else if (bits_per_samp == 16 && num_chan == 2) {
    int32_t data_arr[sample_length];
    fread(data_arr, sizeof(data_arr), 1, file);
    reverse_int32_arr(data_arr, sample_length);
    fseek(file, sizeof(WAVHeader), SEEK_SET);
    fwrite(data_arr, sizeof(data_arr), 1, file);
  }
}

void set_sample_rate(FILE* file, WAVHeader* header, int new_rate) {
  header->samples_per_second = new_rate;
  header->bytes_per_second = header->samples_per_second *
                             (header->bits_per_sample) / 8 *
                             header->number_of_channels;
  fseek(file, 0, SEEK_SET);
  fwrite(header, sizeof(WAVHeader), 1, file);
}

int main(int argc, char** argv) {
  // no arguments given
  if (argc == 1) {
    print_help_msg();
    return 0;
  }
  // we have to read in the wave file in every other situation
  FILE* wavfile = open_file(argv[1]);

  if (!file_exists(wavfile)) {
    fprintf(stderr, "ERROR: File '%s' could not be opened.\n", argv[1]);
    print_help_msg();
    return 1;
  }

  // read the header and make sure we're reading a wav file
  WAVHeader header = read_header(wavfile);
  if (!is_wav_file(&header)) {
    fprintf(stderr,
            "ERROR: Invalid input file. Input file must be a wave file.\n");
    print_help_msg();
    return 1;
  }

  // now what we do depends on arguments
  if (argc == 2) {
    print_wav_info(&header);
  } else if (argc == 3) {
    if (strcmp("-reverse", argv[2]) != 0) {
      print_help_msg();
      return 1;
    }
    reverse_wav(&header, wavfile);
  } else if (argc == 4) {
    if (strcmp("-rate", argv[2]) != 0) {
      print_help_msg();
      return 1;
    }
    int input = atoi(argv[3]);
    if (input <= 0 || input > 192000) {
      print_help_msg();
      return 1;
    }
    set_sample_rate(wavfile, &header, input);
  }

  fclose(wavfile);

  return 0;
}
