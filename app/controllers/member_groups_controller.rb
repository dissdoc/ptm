class MemberGroupsController < ApplicationController
  before_filter :authenticate_user!, :manager?
  before_filter :set_group, :except => [:get_group_join]

  def accept
    join = get_group_join(params)
    join.accept! if join.present?

    redirect_to @group
  end

  def reject
    join = get_group_join(params)
    join.reject! if join.present?

    redirect_to @group
  end

  def remove
    join = get_group_join(params)
    join.destroy if join.present?

    redirect_to @group
  end

  protected

  def set_group
    @group ||= current_user.managing_groups.where(:id => params[:group_id]).first
    redirect_to groups_path if @group.blank?
  end

  def get_group_join(params)
    GroupJoin.where(
      :user_id => params[:id],
      :group_id => params[:group_id],
      :role => 'member').first
  end
end