import React from 'react';

/**
 * Page shown after registration, instructing the user to check their email for a verification link.
 */
const Verification = () => {
  return (
    <div className="verification-container" style={{ maxWidth: 500, margin: '3rem auto', textAlign: 'center' }}>
      <h2>Verify Your Email</h2>
      <p>
        Thank you for registering!<br />
        Please check your email for a verification link to activate your account.
      </p>
      <p style={{ color: '#888', fontSize: '0.95rem' }}>
        If you don't see the email, check your spam or junk folder.<br />
        Still no email? <a href="/resend-verification">Resend verification</a>
      </p>
    </div>
  );
};

export default Verification;
