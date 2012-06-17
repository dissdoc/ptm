class AuthsController < Devise::RegistrationsController
  def create
    super
    session[:omniauth] = nil unless @user.new_record?
  end

  def update
    if resource.update_with_password(params[resource_name])
      set_flash_message :notice, :updated
      sign_in resource_name, resource, :bypass => true
      redirect_to after_update_path_for(resource)
    else
      clean_up_passwords(resource)
      render_with_scope :edit
    end
  end

  private

  def build_resource(*args)
    super
    if session[:omniauth]
      omniauth = session[:omniauth]

      if omniauth['provider'] == 'twitter'
        @first_name = omniauth['info']['name'] if omniauth['info']['name']
        @second_name = nil
        @email = omniauth['info']['email'] if omniauth['info']['email']
        @gender = nil
        @location = omniauth['info']['location'] if omniauth['info']['location']
        @about = omniauth['info']['description'] if omniauth['info']['description']
      elsif omniauth['provider'] = 'facebook'
        @first_name = omniauth['info']['first_name'] if omniauth['info']['first_name']
        @second_name = omniauth['info']['second_name'] if omniauth['info']['second_name']
        @email = omniauth['info']['email'] if omniauth['info']['email']
        @gender = omniauth['extra']['raw_info']['gender'] if omniauth['extra']['raw_info']['gender']
        @location = omniauth['info']['location'] if omniauth['info']['location']
        @about = omniauth['extra']['raw_info']['bio'] if omniauth['extra']['raw_info']['bio']
      end

      @user.apply_omniauth(session[:omniauth])
      @user.valid?
    end
  end
end