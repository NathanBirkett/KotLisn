from pydub import AudioSegment
import sys
import pytube

def match_target_amplitude(sound, target_dBFS):
    change_in_dBFS = target_dBFS - sound.dBFS
    return sound.apply_gain(change_in_dBFS)

def normalize(path):
    sound = AudioSegment.from_wav(path, "wav")
    normalized_sound = match_target_amplitude(sound, -30)
    # increased_sound = normalized_sound + 10
    normalized_sound.export(path, format="wav")