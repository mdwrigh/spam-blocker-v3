import numpy as np
import wave
import struct

def generate_fax_tone():
    """Generate a fax CNG (Calling Tone) - 1100 Hz tone with pauses"""
    sample_rate = 44100
    duration_tone = 0.5  # 500ms tone
    duration_pause = 3.0  # 3 second pause
    frequency = 1100  # Standard fax CNG frequency
    
    # Generate one cycle of tone + pause
    tone_samples = int(sample_rate * duration_tone)
    pause_samples = int(sample_rate * duration_pause)
    
    # Create tone
    t_tone = np.linspace(0, duration_tone, tone_samples, False)
    tone = np.sin(2 * np.pi * frequency * t_tone) * 0.3  # Reduced amplitude
    
    # Create pause (silence)
    pause = np.zeros(pause_samples)
    
    # Combine tone and pause, repeat 10 times (35 seconds total)
    cycle = np.concatenate([tone, pause])
    fax_tone = np.tile(cycle, 10)
    
    # Convert to 16-bit PCM
    fax_tone_int = (fax_tone * 32767).astype(np.int16)
    
    return fax_tone_int, sample_rate

def save_wav_file(filename, audio_data, sample_rate):
    """Save audio data as WAV file"""
    with wave.open(filename, 'w') as wav_file:
        wav_file.setnchannels(1)  # Mono
        wav_file.setsampwidth(2)  # 16-bit
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(audio_data.tobytes())

if __name__ == "__main__":
    print("Generating fax tone...")
    audio_data, sample_rate = generate_fax_tone()
    save_wav_file("spam-call-blocker/app/src/main/res/raw/fax_tone.wav", audio_data, sample_rate)
    print("Fax tone saved as fax_tone.wav")