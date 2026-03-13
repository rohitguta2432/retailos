import { useState, useRef, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Store, ArrowLeft, AlertCircle } from 'lucide-react';
import './Login.css';

const DEV_MODE = import.meta.env.DEV;

export default function Login() {
  const { sendOtp, verifyOtp } = useAuth();
  const [step, setStep] = useState('phone'); // 'phone' | 'otp'
  const [phone, setPhone] = useState('');
  const [otp, setOtp] = useState(['', '', '', '', '', '']);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [resendTimer, setResendTimer] = useState(0);

  const otpRefs = useRef([]);
  const phoneRef = useRef(null);

  // Auto-focus phone input on mount
  useEffect(() => {
    phoneRef.current?.focus();
  }, []);

  // Resend timer countdown
  useEffect(() => {
    if (resendTimer <= 0) return;
    const timer = setTimeout(() => setResendTimer((t) => t - 1), 1000);
    return () => clearTimeout(timer);
  }, [resendTimer]);

  // Auto-focus first OTP input on step change
  useEffect(() => {
    if (step === 'otp') {
      setTimeout(() => otpRefs.current[0]?.focus(), 100);
    }
  }, [step]);

  const fullPhone = `+91${phone}`;

  const handleSendOtp = async (e) => {
    e.preventDefault();
    setError('');

    if (phone.length !== 10) {
      setError('Please enter a valid 10-digit phone number');
      return;
    }

    setLoading(true);
    try {
      if (DEV_MODE) {
        // Dev mode: skip API call, go directly to OTP step
        setStep('otp');
        setResendTimer(30);
      } else {
        await sendOtp(fullPhone);
        setStep('otp');
        setResendTimer(30);
      }
    } catch (err) {
      setError(err.message || 'Failed to send OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleOtpChange = (index, value) => {
    if (!/^\d*$/.test(value)) return;

    const newOtp = [...otp];
    newOtp[index] = value.slice(-1);
    setOtp(newOtp);

    // Auto-advance to next field
    if (value && index < 5) {
      otpRefs.current[index + 1]?.focus();
    }

    // Auto-submit when all 6 digits filled
    if (newOtp.every((d) => d !== '') && value) {
      handleVerifyOtp(newOtp.join(''));
    }
  };

  const handleOtpKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      otpRefs.current[index - 1]?.focus();
    }
  };

  const handleOtpPaste = (e) => {
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (pasted.length === 6) {
      const digits = pasted.split('');
      setOtp(digits);
      otpRefs.current[5]?.focus();
      handleVerifyOtp(pasted);
      e.preventDefault();
    }
  };

  const handleVerifyOtp = async (otpCode) => {
    setError('');
    setLoading(true);
    try {
      if (DEV_MODE) {
        // Dev mode: accept any 6-digit OTP
        const mockUser = {
          id: 'dev-user-001',
          fullName: 'Dev User',
          phone: fullPhone,
          tenantId: 'dev-tenant-001',
          roles: ['OWNER'],
        };
        localStorage.setItem('retailos_token', 'dev-token-mock');
        localStorage.setItem('retailos_user', JSON.stringify(mockUser));
        // Force reload to pick up auth state
        window.location.href = '/';
      } else {
        await verifyOtp(fullPhone, otpCode);
      }
    } catch (err) {
      setError(err.message || 'Invalid OTP. Please try again.');
      setOtp(['', '', '', '', '', '']);
      otpRefs.current[0]?.focus();
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (resendTimer > 0) return;
    setError('');
    setLoading(true);
    try {
      if (!DEV_MODE) await sendOtp(fullPhone);
      setResendTimer(30);
      setOtp(['', '', '', '', '', '']);
      otpRefs.current[0]?.focus();
    } catch (err) {
      setError(err.message || 'Failed to resend OTP');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      {/* Background orbs */}
      <div className="login-bg-orb login-bg-orb--primary" />
      <div className="login-bg-orb login-bg-orb--accent" />
      <div className="login-bg-orb login-bg-orb--secondary" />

      <div className="login-card">
        {/* Brand */}
        <div className="login-brand">
          <div className="login-logo">
            <Store size={32} />
          </div>
          <h1>RetailOS</h1>
          <p>Dukaan ka Smart Partner</p>
        </div>

        {error && (
          <div className="login-error">
            <AlertCircle size={16} />
            {error}
          </div>
        )}

        {/* Step 1: Phone Number */}
        {step === 'phone' && (
          <form className="login-form" onSubmit={handleSendOtp}>
            <div className="login-input-group">
              <label>Phone Number</label>
              <div className="login-phone-wrapper">
                <span className="login-phone-prefix">🇮🇳 +91</span>
                <input
                  ref={phoneRef}
                  id="login-phone"
                  type="tel"
                  className="login-phone-input"
                  placeholder="9876543210"
                  maxLength={10}
                  value={phone}
                  onChange={(e) => {
                    const v = e.target.value.replace(/\D/g, '');
                    setPhone(v);
                    setError('');
                  }}
                  autoComplete="tel"
                />
              </div>
            </div>

            <button
              id="send-otp-btn"
              type="submit"
              className="login-submit"
              disabled={loading || phone.length !== 10}
            >
              {loading ? <div className="login-spinner" /> : 'Send OTP'}
            </button>

            {DEV_MODE && (
              <div className="login-dev-hint">
                <strong>Dev Mode:</strong> Any 10-digit number + any 6-digit OTP will work
              </div>
            )}
          </form>
        )}

        {/* Step 2: OTP Verification */}
        {step === 'otp' && (
          <div className="login-form">
            <div className="login-otp-sent">
              OTP sent to <span>{fullPhone}</span>
            </div>

            <div className="login-input-group">
              <label style={{ textAlign: 'center' }}>Enter 6-digit OTP</label>
              <div className="login-otp-group" onPaste={handleOtpPaste}>
                {otp.map((digit, idx) => (
                  <input
                    key={idx}
                    ref={(el) => (otpRefs.current[idx] = el)}
                    id={`otp-digit-${idx}`}
                    type="tel"
                    className="login-otp-digit"
                    maxLength={1}
                    value={digit}
                    onChange={(e) => handleOtpChange(idx, e.target.value)}
                    onKeyDown={(e) => handleOtpKeyDown(idx, e)}
                    autoComplete="one-time-code"
                  />
                ))}
              </div>
            </div>

            <button
              id="verify-otp-btn"
              type="button"
              className="login-submit"
              disabled={loading || otp.some((d) => d === '')}
              onClick={() => handleVerifyOtp(otp.join(''))}
            >
              {loading ? <div className="login-spinner" /> : 'Verify & Login'}
            </button>

            <div className="login-footer">
              <button
                className="login-link"
                disabled={resendTimer > 0}
                onClick={handleResend}
                style={{ marginRight: 16 }}
              >
                {resendTimer > 0 ? `Resend in ${resendTimer}s` : 'Resend OTP'}
              </button>
              <button
                className="login-link"
                onClick={() => {
                  setStep('phone');
                  setOtp(['', '', '', '', '', '']);
                  setError('');
                }}
              >
                <ArrowLeft size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} />
                Change Number
              </button>
            </div>

            {DEV_MODE && (
              <div className="login-dev-hint">
                <strong>Dev Mode:</strong> Enter any 6 digits to login
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
