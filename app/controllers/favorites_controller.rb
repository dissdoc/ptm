class FavoritesController < ApplicationController
  before_filter :authenticate_user!
  before_filter :can_fave?

  def fave
    current_user.create!(:photo_id => params[:photo_id])
    respond_to do |format|
      format.js
    end
  end

  def unfave
    @favorite = current_user.favorites.find(params[:photo_id])
    @favorite.destroy
    respond_to do |format|
      format.js
    end
  end

  protected

  def can_fave?
    !current_user.photos.find(params[:photo_id]).present?
  end
end