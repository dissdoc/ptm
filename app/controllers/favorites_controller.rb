class FavoritesController < ApplicationController
  before_filter :authenticate_user!, :can_fave?, :set_photo

  def fave
    current_user.favorites.create!(:photo_id => params[:photo_id])
    photo = Photo.find(params[:photo_id])
    UserMailer.added_photo_favorite(current_user, photo).deliver
    Activity.add(photo.user, photo, Activity::ADD_FAVE_PHOTO, current_user)
    respond_to do |format|
      format.js
    end
  end

  def unfave
    @favorite = current_user.favorites.where(:photo_id => params[:photo_id]).first
    @favorite.destroy
    respond_to do |format|
      format.js { render :template => 'favorites/fave.js.erb' }
    end
  end

  protected

  def can_fave?
    !current_user.photos.where('id = ?', params[:photo_id]).first.present?
  end

  def set_photo
    @photo = Photo.find(params[:photo_id]) if params[:photo_id].present?
  end
end