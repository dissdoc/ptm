ActionMailer::Base.smtp_settings = {
    :address  => "smtp.gmail.com",
    :port  => 587,
    :domain => 'phototimemachine.org',
    :user_name  => "hello@phototimemachine.org",
    :password  => "onskazalpoehali1",
    :authentication  => 'plain',
    :enable_starttls_auto => true
}