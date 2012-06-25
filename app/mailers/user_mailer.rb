class UserMailer < ActionMailer::Base
  default :from => 'hello@phototimemachine.org'

  def added_photo_favorite(user, photo)
    @user = user
    @photo = photo
    mail(
        :to => photo.user.email,
        :subject => "#{user.full_name} added your photo '#{photo.comment}' as a favorite"
    )
  end
end
