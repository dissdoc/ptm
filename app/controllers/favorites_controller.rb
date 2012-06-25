class FavoritesController < ApplicationController
  before_filter :authenticate_user!, :can_fave?, :set_photo

  def fave
    current_user.favorites.create!(:photo_id => params[:photo_id])
    respond_to do |format|
      format.js
    end

    photo = Photo.find(params[:photo_id])
    UserMailer.deliver_added_photo_favorite(current_user, photo)
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